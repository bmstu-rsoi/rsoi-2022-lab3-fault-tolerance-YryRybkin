package bmstu.rybkin.lab3.hbs.paymentapplication.service;

import bmstu.rybkin.lab3.hbs.paymentapplication.models.PaymentInfo;

import java.util.UUID;

public interface PaymentService {

    UUID postPayment(UUID paymentUid, Integer price);
    PaymentInfo getPayment(UUID paymentUid);
    PaymentInfo cancelPayment(UUID paymentUid);

    void deletePayment(UUID paymentUid);

}
