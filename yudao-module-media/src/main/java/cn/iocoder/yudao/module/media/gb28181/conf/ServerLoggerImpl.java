package cn.iocoder.yudao.module.media.gb28181.conf;

import gov.nist.core.CommonLogger;
import gov.nist.core.ServerLogger;
import gov.nist.core.StackLogger;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.stack.SIPTransactionStack;

import javax.sip.SipStack;
import java.util.Properties;

public class ServerLoggerImpl implements ServerLogger {

    private boolean showLog = false;

    private SIPTransactionStack sipStack;

    protected StackLogger stackLogger;

    @Override
    public void closeLogFile() {

    }

    @Override
    public void logMessage(SIPMessage message, String from, String to, boolean sender, long time) {
        if (!showLog || this.stackLogger == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sender? "发送：目标--->" + from:"接收：来自--->" + to)
                .append("\r\n")
                        .append(message);
        this.stackLogger.logInfo(stringBuilder.toString());

    }

    @Override
    public void logMessage(SIPMessage message, String from, String to, String status, boolean sender, long time) {
        if (!showLog || this.stackLogger == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sender? "发送： 目标->" + from :"接收：来自->" + to)
                .append("\r\n")
                .append(message);
        this.stackLogger.logInfo(stringBuilder.toString());
    }

    @Override
    public void logMessage(SIPMessage message, String from, String to, String status, boolean sender) {
        if (!showLog || this.stackLogger == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sender? "发送： 目标->" + from :"接收：来自->" + to)
                .append("\r\n")
                .append(message);
        this.stackLogger.logInfo(stringBuilder.toString());
    }

    @Override
    public void logException(Exception ex) {
        if (!showLog || this.stackLogger == null) {
            return;
        }
        this.stackLogger.logException(ex);
    }

    @Override
    public void setStackProperties(Properties stackProperties) {
        String logMessageContent = stackProperties.getProperty("gov.nist.javax.sip.LOG_MESSAGE_CONTENT");
        showLog = "true".equalsIgnoreCase(logMessageContent);
    }

    @Override
    public void setSipStack(SipStack sipStack) {
        if(sipStack instanceof SIPTransactionStack) {
            this.sipStack = (SIPTransactionStack)sipStack;
            this.stackLogger = CommonLogger.getLogger(SIPTransactionStack.class);
        }
    }
}
