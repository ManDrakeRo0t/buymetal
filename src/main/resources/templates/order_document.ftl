<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Докумет сделки</title>
    <style>
        body {
            font-family: Arial;
            height: 842px;
            width: 595px;
            /* to centre page on screen*/
            margin-left: auto;
            margin-right: auto;
        }
    </style>
</head>
<body>
<header>
    <h3>Документ о завершении сделки от ${order.creationTime}</h3>
    <p>1. Уведомление о завершении заказа № ${orderNumber} между заказчиком ${customer.companyName} и поставщиком ${supplier.companyName}.</p>
    <p>Стороны согласовали, что общая сумма сделки составляет ${applicationResponse.fullPrice} рублей. Договор полностью оплачен и поставка произведена.</p>
</header>
<div>
    <div style="display: flex; flex-direction: row; justify-content: space-between">
        <div style=" width: 45%; padding : 10px ;border-radius: 20px; border: 2px solid black">
            <h4 ><strong> Заказчик :</strong> ${customer.companyName}</h4>
            <h5 style="margin-top: 5px; margin-bottom: 5px"><strong> Адреc :</strong> ${customer.companyAddress}</h5>
            <h5 style="margin-top: 5px; margin-bottom: 5px"><strong> ИНН :</strong> ${customer.tin}</h5>
        </div>


        <div style=" width: 45%; padding : 10px ;border-radius: 20px; border: 2px solid black">
            <h4><strong> Поставщик :</strong> ${supplier.companyName}</h4>
            <h5 style="margin-top: 5px; margin-bottom: 5px"><strong> Адреc :</strong> ${supplier.companyAddress}</h5>
            <h5 style="margin-top: 5px; margin-bottom: 5px"><strong> ИНН :</strong> ${supplier.tin}</h5>
        </div>
    </div>
</div>
<div style=" padding : 10px; margin-top: 20px ; border-radius: 20px; border: 2px solid black">
    <div style="display: flex; flex-direction: row">
        <div style="width: 50%">
            <h3>Информация о сделке</h3>
            <div style="">
                <h4><strong> Предмет сделки</strong></h4>
                <p style="margin: 2px">
                    Форма : ${applicationResponse.rolledForm}
                </p>
                <p style="margin: 2px">
                    Тип : ${applicationResponse.rolledType}
                </p>
                <p style="margin: 2px">
                    Размер : ${applicationResponse.rolledSize}
                </p>
                <p style="margin: 2px">
                    Гост : ${applicationResponse.rolledGost}
                </p>
            </div>
        </div>
        <div style="width: 50%">
            <h3>Рассчет стоимости</h3>
            <div style="">
                <h4><strong>Колличество ${applicationResponse.amount} шт.</strong></h4>
                <p style="margin: 2px">
                    Стоимость за шт. : <strong>${applicationResponse.price} руб.</strong>
                </p>
                <p style="margin: 2px">
                    Общая cумма : <strong style="text-align: right">${applicationResponse.fullPrice} руб.</strong>
                </p>
                <p style="margin: 2px">
                    ___________________________
                </p>
                <p style="margin: 2px">
                    Коммисия сервиса : <strong>${applicationResponse.fullPrice * 0.01} руб.</strong>
                </p>
            </div>
        </div>
    </div>
    <div>
        <#if order.creationTime??>
            <p style="margin-bottom: 5px"> Дата начала сделки : ${order.creationTime}</p>
        </#if>
        <#if order.agreementDate??>
            <p style="margin-bottom: 5px"> Дата согласования : ${order.agreementDate}</p>
        </#if>
        <#if order.startDeliveryDate??>
            <p style="margin-bottom: 5px"> Дата начала доставки : ${order.startDeliveryDate}</p>
        </#if>
        <#if order.paymentDate??>
            <p style="margin-bottom: 5px"> Дата отплаты комиссии : ${order.paymentDate}</p>
        </#if>
        <#if order.completeDate??>
            <p style="margin-bottom: 5px"> Дата завершения заказа : ${order.completeDate}</p>
        </#if>
        <#if order.rejectDate??>
            <p style="margin-bottom: 5px"> Дата отмены заказа : ${order.rejectDate}</p>
            <#if order.canceledByCustomer>
                <p style="margin-bottom: 5px">  Заказ отменен заказчиком</p>
            <#else >
                <p style="margin-bottom: 5px">  Заказ отменен поставщиком</p>
            </#if>
        </#if>
    </div>
</div>
<div>
    <p>2. Поставщик обязуется доставить предмет договора в соответствии с условиями, согласованными Сторонами, а Заказчик обязуется принять и оплатить товары/услуги в установленные сроки и порядке.</p>
    <p>Согласно правилам использования мобильного приложения АнМеталл, заказчик - ООО "АнМеталл" обязуется оплатить коммиссию в размере 1% от суммы общий сделки.</p>

    <p>3. Мобильное приложение "АнМеталл" взимает комиссию в размере 1% от общей суммы сделки, оплата коммисии будет произведена отдельно внутри мобильного приложения "АнМеталл".</p>
    <p>Заказчик используя мобильное приложение "АнМеталл" соглашается с тем, что взимается комиссию в размере 1% от общей суммы сделки.</p>
</div>
<footer >
    <p><strong>Контактные данные ООО "АнМеталл" (владелец мобильного приложения):</strong></p>
    <p><strong>Юридический номер:</strong> (Юридический номер)</p>
</footer>
</body>
</html>