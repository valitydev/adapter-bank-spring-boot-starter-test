package com.rbkmoney.adapter.bank.spring.boot.starter.test;

import com.rbkmoney.adapter.bank.spring.boot.starter.test.constants.Path;
import com.rbkmoney.adapter.bank.spring.boot.starter.test.constants.Postfix;
import com.rbkmoney.adapter.bank.spring.boot.starter.test.utils.SaveIntegrationFileUtils;
import com.rbkmoney.damsel.proxy_provider.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Component
public class ServerHandlerFileReaderDecorator implements ProviderProxySrv.Iface {

    private final ProviderProxySrv.Iface serverHandlerLogDecorator;

    private ThreadLocal<AtomicInteger> generateCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> handleRecurrentTokenCallbackCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> processPaymentCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> handlePaymentCallbackCount = ThreadLocal.withInitial(AtomicInteger::new);

    @Override
    public RecurrentTokenProxyResult generateToken(RecurrentTokenContext context) throws TException {
        RecurrentTokenContext recurrentTokenContext = new RecurrentTokenContext();
        int count = generateCount.get().incrementAndGet();
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        readTBase(methodName, recurrentTokenContext, count);

        return serverHandlerLogDecorator.generateToken(recurrentTokenContext);
    }

    @Override
    public RecurrentTokenCallbackResult handleRecurrentTokenCallback(ByteBuffer byteBuffer, RecurrentTokenContext context) throws TException {
        RecurrentTokenContext recurrentTokenContext = new RecurrentTokenContext();
        int count = handleRecurrentTokenCallbackCount.get().incrementAndGet();
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        readTBase(methodName, recurrentTokenContext, count);

        byte[] bytes1 = SaveIntegrationFileUtils.readFile(methodName + Postfix.BYTE_BUFFER, Path.SRC_TEST_RESOURCES_GENERATED_HG, count);

        return serverHandlerLogDecorator.handleRecurrentTokenCallback(ByteBuffer.wrap(bytes1), recurrentTokenContext);
    }

    @Override
    public PaymentProxyResult processPayment(PaymentContext context) throws TException {
        PaymentContext newContext = new PaymentContext();
        int count = processPaymentCount.get().incrementAndGet();
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        readTBase(methodName, newContext, count);

        return serverHandlerLogDecorator.processPayment(newContext);
    }

    private <T extends TBase> T readTBase(String methodName, T newContext, int count) throws TException {
        byte[] bytes = SaveIntegrationFileUtils.readFile(methodName + Postfix.REQUEST, Path.SRC_TEST_RESOURCES_GENERATED_HG, count);
        TDeserializer tDeserializer = new TDeserializer();
        tDeserializer.deserialize(newContext, bytes);
        return newContext;
    }

    @Override
    public PaymentCallbackResult handlePaymentCallback(ByteBuffer byteBuffer, PaymentContext context) throws TException {
        PaymentContext contextNew = new PaymentContext();
        int count = handlePaymentCallbackCount.get().incrementAndGet();
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        readTBase(methodName, contextNew, count);

        byte[] bytes1 = SaveIntegrationFileUtils.readFile(methodName + Postfix.BYTE_BUFFER, Path.SRC_TEST_RESOURCES_GENERATED_HG, count);

        return serverHandlerLogDecorator.handlePaymentCallback(ByteBuffer.wrap(bytes1), contextNew);
    }

}
