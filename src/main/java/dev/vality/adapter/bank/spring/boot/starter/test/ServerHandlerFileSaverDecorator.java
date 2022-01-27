package dev.vality.adapter.bank.spring.boot.starter.test;

import dev.vality.adapter.bank.spring.boot.starter.test.constants.Path;
import dev.vality.adapter.bank.spring.boot.starter.test.constants.Postfix;
import dev.vality.adapter.bank.spring.boot.starter.test.utils.SaveIntegrationFileUtils;
import dev.vality.damsel.proxy_provider.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Component
public class ServerHandlerFileSaverDecorator implements ProviderProxySrv.Iface {

    private final ProviderProxySrv.Iface serverHandlerLogDecorator;

    private ThreadLocal<AtomicInteger> generateCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> handleRecurrentTokenCallbackCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> processPaymentCount = ThreadLocal.withInitial(AtomicInteger::new);
    private ThreadLocal<AtomicInteger> handlePaymentCallbackCount = ThreadLocal.withInitial(AtomicInteger::new);

    @Override
    public RecurrentTokenProxyResult generateToken(RecurrentTokenContext context) throws TException {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        int count = saveRequest(context, methodName, generateCount);
        RecurrentTokenProxyResult recurrentTokenProxyResult = serverHandlerLogDecorator.generateToken(context);
        return saveResult(methodName, count, recurrentTokenProxyResult);
    }

    private <T extends TBase> T saveResult(String methodName, int count, T thriftBase) throws TException {
        TSerializer serializerResp = new TSerializer();
        byte[] serializeResult = serializerResp.serialize(thriftBase);
        SaveIntegrationFileUtils.saveFile(serializeResult,
                methodName + Postfix.RESULT,
                Path.SRC_TEST_RESOURCES_GENERATED_HG,
                count);
        return thriftBase;
    }

    @Override
    public RecurrentTokenCallbackResult handleRecurrentTokenCallback(ByteBuffer byteBuffer,
                                                                     RecurrentTokenContext context) throws TException {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        int count = saveRequest(context, methodName, handleRecurrentTokenCallbackCount);
        SaveIntegrationFileUtils
                .saveFile(byteBuffer.array(), methodName + Postfix.BYTE_BUFFER, Path.SRC_TEST_RESOURCES_GENERATED_HG,
                        count);
        RecurrentTokenCallbackResult recurrentTokenCallbackResult =
                serverHandlerLogDecorator.handleRecurrentTokenCallback(byteBuffer, context);
        return saveResult(methodName, count, recurrentTokenCallbackResult);
    }

    @Override
    public PaymentProxyResult processPayment(PaymentContext context) throws TException {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        int count = saveRequest(context, methodName, processPaymentCount);
        PaymentProxyResult paymentProxyResult = serverHandlerLogDecorator.processPayment(context);
        return saveResult(methodName, count, paymentProxyResult);
    }

    @Override
    public PaymentCallbackResult handlePaymentCallback(ByteBuffer byteBuffer, PaymentContext context)
            throws TException {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        int count = saveRequest(context, methodName, handlePaymentCallbackCount);
        SaveIntegrationFileUtils
                .saveFile(byteBuffer.array(), methodName + Postfix.BYTE_BUFFER, Path.SRC_TEST_RESOURCES_GENERATED_HG,
                        count);
        PaymentCallbackResult paymentCallbackResult =
                serverHandlerLogDecorator.handlePaymentCallback(byteBuffer, context);
        return saveResult(methodName, count, paymentCallbackResult);
    }

    private <T extends TBase> int saveRequest(T context, String methodName, ThreadLocal<AtomicInteger> counter)
            throws TException {
        TSerializer serializer = new TSerializer();
        byte[] serializeRequest = serializer.serialize(context);
        int count = counter.get().incrementAndGet();
        SaveIntegrationFileUtils
                .saveFile(serializeRequest, methodName + Postfix.REQUEST, Path.SRC_TEST_RESOURCES_GENERATED_HG, count);
        return count;
    }

}
