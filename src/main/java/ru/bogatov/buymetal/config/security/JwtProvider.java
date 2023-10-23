package ru.bogatov.buymetal.config.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bogatov.buymetal.entity.User;
import ru.bogatov.buymetal.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    String SECRET;
    @Value("${jwt.token.access.expire}")
    Integer ACCESS_TOKEN_EXPIRE;
    @Value("${jwt.token.refresh.expire}")
    Integer REFRESH_TOKEN_EXPIRE;
    private final static String ACCOUNT_POSITION = "position";
    private final static String ID = "id";

    private final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    private final UserService userService;

    public JwtProvider(UserService userService) {
        this.userService = userService;
    }

    public String generateTokenForUser(User user) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE).atZone(ZoneId.systemDefault()).toInstant());
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(ID, user.getId());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512,SECRET)
                .compact();
    }

    public String generateTokenForUser(UUID userId) {
        User user = userService.findById(userId);
        return generateTokenForUser(user);
    }

    @Transactional
    public String generateRefreshForUser(User user) {
        Date date = Date.from(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE).atZone(ZoneId.systemDefault()).toInstant());
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(ID,user.getId());
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512,SECRET)
                .compact();
        userService.updateRefresh(user.getId(), token);
        return token;
    }


    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
            return true;
        }catch (ExpiredJwtException expEx) {
            logger.warn("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            logger.warn("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            logger.warn("Malformed jwt");
        } catch (SignatureException sEx) {
            logger.warn("Invalid signature");
        } catch (Exception e) {
            logger.warn("invalid token");
        }
        return false;
    }

    public String getUserIdFromToken(String token){
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.get(ID).toString();
    }
}

