package com.rbkmoney.adapter.bank.spring.boot.starter.test;

import com.rbkmoney.adapter.bank.spring.boot.starter.test.utils.SaveIntegrationFileUtils;
import com.rbkmoney.damsel.proxy_provider.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Component
public class ServerHandlerFileReaderDecorator implements ProviderProxySrv.Iface {

    public static final String SRC_TEST_RESOURCES_GENERATED_HG = "src/test/resources/generated/hg/";
    private final ProviderProxySrv.Iface serverHandlerLogDecorator;

    private ThreadLocal<AtomicInteger> generateCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> handleRecurrentTokenCallbackCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> processPaymentCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> handlePaymentCallbackCount = ThreadLocal.withInitial(AtomicInteger::new);

    @Override
    public RecurrentTokenProxyResult generateToken(RecurrentTokenContext context) throws TException {
        String methodName = "generateToken";
        int count = generateCount.get().incrementAndGet();
        byte[] bytes = SaveIntegrationFileUtils.readFile(methodName + "Response", SRC_TEST_RESOURCES_GENERATED_HG, count);

        TDeserializer tDeserializer = new TDeserializer();
        RecurrentTokenContext recurrentTokenContext = new RecurrentTokenContext();
        tDeserializer.deserialize(recurrentTokenContext, bytes);
        return serverHandlerLogDecorator.generateToken(recurrentTokenContext);
    }

    @Override
    public RecurrentTokenCallbackResult handleRecurrentTokenCallback(ByteBuffer byteBuffer, RecurrentTokenContext context) throws TException {
        String methodName = "handleRecurrentTokenCallback";
        int count = handleRecurrentTokenCallbackCount.get().incrementAndGet();

        byte[] bytes = SaveIntegrationFileUtils.readFile(methodName + "Response", SRC_TEST_RESOURCES_GENERATED_HG, count);
        TDeserializer tDeserializer = new TDeserializer();
        RecurrentTokenContext recurrentTokenContext = new RecurrentTokenContext();
        tDeserializer.deserialize(recurrentTokenContext, bytes);
        byte[] bytes1 = SaveIntegrationFileUtils.readFile(methodName + "ByteBuffer", SRC_TEST_RESOURCES_GENERATED_HG, count);

        return serverHandlerLogDecorator.handleRecurrentTokenCallback(ByteBuffer.wrap(bytes1), recurrentTokenContext);
    }

    @Override
    public PaymentProxyResult processPayment(PaymentContext context) throws TException {
        String methodName = "processPayment";
        int count = processPaymentCount.get().incrementAndGet();

        byte[] bytes = SaveIntegrationFileUtils.readFile(methodName + "Response", SRC_TEST_RESOURCES_GENERATED_HG, count);
        TDeserializer tDeserializer = new TDeserializer();
        PaymentContext newContext = new PaymentContext();
        tDeserializer.deserialize(newContext, bytes);

        return serverHandlerLogDecorator.processPayment(newContext);
    }

    @Override
    public PaymentCallbackResult handlePaymentCallback(ByteBuffer byteBuffer, PaymentContext context) throws TException {
        TSerializer serializer = new TSerializer();
        byte[] serializeRequest = serializer.serialize(context);
        String methodName = "handlePaymentCallback";
        int count = handlePaymentCallbackCount.get().incrementAndGet();

        byte[] bytes = SaveIntegrationFileUtils.readFile(methodName + "Response", SRC_TEST_RESOURCES_GENERATED_HG, count);
        TDeserializer tDeserializer = new TDeserializer();
        PaymentContext contextNew = new PaymentContext();
        tDeserializer.deserialize(contextNew, bytes);
        byte[] bytes1 = SaveIntegrationFileUtils.readFile(methodName + "ByteBuffer", SRC_TEST_RESOURCES_GENERATED_HG, count);

        return serverHandlerLogDecorator.handlePaymentCallback(ByteBuffer.wrap(bytes1), contextNew);
    }

}
