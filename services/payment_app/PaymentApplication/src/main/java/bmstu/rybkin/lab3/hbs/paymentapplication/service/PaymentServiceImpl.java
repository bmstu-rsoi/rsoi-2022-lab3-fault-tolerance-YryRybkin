package bmstu.rybkin.lab3.hbs.paymentapplication.service;

import bmstu.rybkin.lab3.hbs.paymentapplication.jpa.Payment;
import bmstu.rybkin.lab3.hbs.paymentapplication.jpa.PaymentRepo;
import bmstu.rybkin.lab3.hbs.paymentapplication.models.PaymentInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;


@Service
public class PaymentServiceImpl implements PaymentService {


    private final PaymentRepo paymentRepo;

    PaymentServiceImpl(PaymentRepo paymentRepo) {

        this.paymentRepo = paymentRepo;

    }

    @Transactional
    @Override
    public UUID postPayment(UUID paymentUid, Integer price) {

        if (price <= 0) {

            throw new IllegalStateException("Price must be greater than zero");

        }
        Payment payment = new Payment(paymentUid, "PAID", price);
        Payment posted = paymentRepo.save(payment);
        return posted.getPaymentUid();

    }

    @Transactional(readOnly = true)
    @Override
    public PaymentInfo getPayment(UUID paymentUid) {

        Payment payment = paymentRepo.findByPaymentUid(paymentUid).orElseThrow(
                () -> new EntityNotFoundException(String.format("Payment with uid: %s not found", paymentUid.toString())));
        return buildPaymentInfo(payment.getStatus(), payment.getPrice());

    }

    @Transactional
    @Override
    public PaymentInfo cancelPayment(UUID paymentUid) {

        Payment payment = paymentRepo.findByPaymentUid(paymentUid).orElseThrow(
                () -> new EntityNotFoundException(String.format("Payment with uid: %s not found", paymentUid.toString())));
        if (payment.getStatus().equals("CANCELED")) {

            throw new IllegalStateException(String.format("Payment with uid: %s is already canceled", paymentUid.toString()));

        }
        Payment patched = new Payment(payment.getId(), payment.getPaymentUid(), "CANCELED", payment.getPrice());
        paymentRepo.save(patched);
        return buildPaymentInfo(patched.getStatus(), patched.getPrice());

    }

    @Transactional
    @Override
    public void deletePayment(UUID paymentUid) {

        paymentRepo.deleteByPaymentUid(paymentUid);

    }

    PaymentInfo buildPaymentInfo(String status, Integer price) {

        return new PaymentInfo(status, price);

    }

}
