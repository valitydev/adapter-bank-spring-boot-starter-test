package com.rbkmoney.adapter.bank.spring.boot.starter.test;

import com.rbkmoney.adapter.bank.spring.boot.starter.test.utils.SaveIntegrationFileUtils;
import com.rbkmoney.damsel.proxy_provider.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Component
public class ServerHandlerFileSaverDecorator implements ProviderProxySrv.Iface {

    public static final String SRC_TEST_RESOURCES_GENERATED_HG = "src/test/resources/generated/hg/";
    private final ProviderProxySrv.Iface serverHandlerLogDecorator;

    private ThreadLocal<AtomicInteger> generateCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> handleRecurrentTokenCallbackCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> processPaymentCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> handlePaymentCallbackCount = ThreadLocal.withInitial(AtomicInteger::new);

    @Override
    public RecurrentTokenProxyResult generateToken(RecurrentTokenContext context) throws TException {
        TSerializer serializer = new TSerializer();
        byte[] serializeRequest = serializer.serialize(context);
        String methodName = "generateToken";
        int count = generateCount.get().incrementAndGet();
        SaveIntegrationFileUtils.saveFile(serializeRequest, methodName + "Response", SRC_TEST_RESOURCES_GENERATED_HG, count);
        RecurrentTokenProxyResult recurrentTokenProxyResult = serverHandlerLogDecorator.generateToken(context);
        byte[] serializeResult = serializer.serialize(recurrentTokenProxyResult);
        SaveIntegrationFileUtils.saveFile(serializeResult, methodName + "Result", SRC_TEST_RESOURCES_GENERATED_HG, count);
        return recurrentTokenProxyResult;
    }

    @Override
    public RecurrentTokenCallbackResult handleRecurrentTokenCallback(ByteBuffer byteBuffer, RecurrentTokenContext context) throws TException {
        TSerializer serializer = new TSerializer();
        byte[] serializeRequest = serializer.serialize(context);
        String methodName = "handleRecurrentTokenCallback";
        int count = handleRecurrentTokenCallbackCount.get().incrementAndGet();

        SaveIntegrationFileUtils.saveFile(serializeRequest, methodName + "Response", SRC_TEST_RESOURCES_GENERATED_HG, count);
        SaveIntegrationFileUtils.saveFile(byteBuffer.array(), methodName + "ByteBuffer", SRC_TEST_RESOURCES_GENERATED_HG, count);
        RecurrentTokenCallbackResult recurrentTokenCallbackResult = serverHandlerLogDecorator.handleRecurrentTokenCallback(byteBuffer, context);
        byte[] serializeResult = serializer.serialize(recurrentTokenCallbackResult);
        SaveIntegrationFileUtils.saveFile(serializeResult, methodName + "Result", SRC_TEST_RESOURCES_GENERATED_HG, count);
        return recurrentTokenCallbackResult;
    }

    @Override
    public PaymentProxyResult processPayment(PaymentContext context) throws TException {
        TSerializer serializer = new TSerializer();
        byte[] serializeRequest = serializer.serialize(context);
        String methodName = "processPayment";
        int count = processPaymentCount.get().incrementAndGet();

        SaveIntegrationFileUtils.saveFile(serializeRequest, methodName + "Response", SRC_TEST_RESOURCES_GENERATED_HG, count);
        PaymentProxyResult paymentProxyResult = serverHandlerLogDecorator.processPayment(context);
        byte[] serializeResult = serializer.serialize(paymentProxyResult);
        SaveIntegrationFileUtils.saveFile(serializeResult, methodName + "Result", SRC_TEST_RESOURCES_GENERATED_HG, count);
        return paymentProxyResult;
    }

    @Override
    public PaymentCallbackResult handlePaymentCallback(ByteBuffer byteBuffer, PaymentContext context) throws TException {
        TSerializer serializer = new TSerializer();
        byte[] serializeRequest = serializer.serialize(context);
        String methodName = "handlePaymentCallback";
        int count = handlePaymentCallbackCount.get().incrementAndGet();

        SaveIntegrationFileUtils.saveFile(serializeRequest, methodName + "Response", SRC_TEST_RESOURCES_GENERATED_HG, count);
        SaveIntegrationFileUtils.saveFile(byteBuffer.array(), methodName + "ByteBuffer", SRC_TEST_RESOURCES_GENERATED_HG, count);
        PaymentCallbackResult paymentCallbackResult = serverHandlerLogDecorator.handlePaymentCallback(byteBuffer, context);
        byte[] serializeResult = serializer.serialize(paymentCallbackResult);
        SaveIntegrationFileUtils.saveFile(serializeResult, methodName + "Result", SRC_TEST_RESOURCES_GENERATED_HG, count);
        return paymentCallbackResult;
    }

}
