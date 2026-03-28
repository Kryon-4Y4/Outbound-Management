package com.wms.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 打印机工具类
 * 提供打印机连接、状态检测、打印等功能
 */
@Slf4j
@Component
public class PrinterUtil {

    /**
     * 连接超时时间（毫秒）
     */
    private static final int CONNECT_TIMEOUT = 5000;
    
    /**
     * 读取超时时间（毫秒）
     */
    private static final int READ_TIMEOUT = 3000;

    /**
     * 检测网络打印机是否在线
     *
     * @param ip   打印机IP地址
     * @param port 打印机端口（默认9100）
     * @return 是否在线
     */
    public boolean isPrinterOnline(String ip, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT);
            return socket.isConnected();
        } catch (IOException e) {
            log.warn("打印机连接失败: {}:{}, 错误: {}", ip, port, e.getMessage());
            return false;
        }
    }

    /**
     * 发送ZPL指令到网络打印机
     *
     * @param ip      打印机IP地址
     * @param port    打印机端口
     * @param zplCode ZPL指令
     * @return 是否发送成功
     */
    public boolean sendToNetworkPrinter(String ip, int port, String zplCode) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT);
            socket.setSoTimeout(READ_TIMEOUT);
            
            OutputStream out = socket.getOutputStream();
            out.write(zplCode.getBytes(StandardCharsets.UTF_8));
            out.flush();
            
            log.info("ZPL指令已发送至打印机: {}:{}", ip, port);
            return true;
        } catch (IOException e) {
            log.error("发送打印指令失败: {}:{}, 错误: {}", ip, port, e.getMessage());
            return false;
        }
    }

    /**
     * 获取打印机状态（Zebra打印机）
     *
     * @param ip   打印机IP地址
     * @param port 打印机端口
     * @return 状态信息
     */
    public String getPrinterStatus(String ip, int port) {
        // Zebra ~HS 指令获取状态
        String statusCommand = "~HS";
        
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT);
            socket.setSoTimeout(READ_TIMEOUT);
            
            OutputStream out = socket.getOutputStream();
            out.write(statusCommand.getBytes(StandardCharsets.UTF_8));
            out.flush();
            
            // 读取响应
            byte[] buffer = new byte[1024];
            int bytesRead = socket.getInputStream().read(buffer);
            if (bytesRead > 0) {
                return new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            }
            return null;
        } catch (IOException e) {
            log.error("获取打印机状态失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析Zebra打印机状态
     *
     * @param statusResponse 状态响应字符串
     * @return 状态对象
     */
    public PrinterStatus parseZebraStatus(String statusResponse) {
        if (statusResponse == null || statusResponse.isEmpty()) {
            return null;
        }
        
        PrinterStatus status = new PrinterStatus();
        String[] parts = statusResponse.split(",");
        
        if (parts.length >= 3) {
            // 解析状态字节
            int statusByte1 = Integer.parseInt(parts[0].trim());
            int statusByte2 = Integer.parseInt(parts[1].trim());
            
            // 状态字节1解析
            status.setPaperOut((statusByte1 & 0x01) != 0);
            status.setPause((statusByte1 & 0x02) != 0);
            status.setRibbonOut((statusByte1 & 0x04) != 0);
            status.setThermalTransferMode((statusByte1 & 0x08) != 0);
            
            // 状态字节2解析
            status.setBufferFull((statusByte2 & 0x01) != 0);
            status.setDiagnosticMode((statusByte2 & 0x02) != 0);
            status.setPaperFeed((statusByte2 & 0x04) != 0);
        }
        
        return status;
    }

    /**
     * 校准打印机（Zebra）
     *
     * @param ip   打印机IP地址
     * @param port 打印机端口
     * @return 是否成功
     */
    public boolean calibratePrinter(String ip, int port) {
        // Zebra ~JC 指令进行介质校准
        return sendToNetworkPrinter(ip, port, "~JC");
    }

    /**
     * 打印机状态对象
     */
    public static class PrinterStatus {
        private boolean paperOut;           // 缺纸
        private boolean pause;              // 暂停
        private boolean ribbonOut;          // 碳带用尽
        private boolean thermalTransferMode; // 热转印模式
        private boolean bufferFull;         // 缓冲区满
        private boolean diagnosticMode;     // 诊断模式
        private boolean paperFeed;          // 走纸中
        private boolean online;             // 是否在线

        // Getters and Setters
        public boolean isPaperOut() { return paperOut; }
        public void setPaperOut(boolean paperOut) { this.paperOut = paperOut; }
        
        public boolean isPause() { return pause; }
        public void setPause(boolean pause) { this.pause = pause; }
        
        public boolean isRibbonOut() { return ribbonOut; }
        public void setRibbonOut(boolean ribbonOut) { this.ribbonOut = ribbonOut; }
        
        public boolean isThermalTransferMode() { return thermalTransferMode; }
        public void setThermalTransferMode(boolean thermalTransferMode) { this.thermalTransferMode = thermalTransferMode; }
        
        public boolean isBufferFull() { return bufferFull; }
        public void setBufferFull(boolean bufferFull) { this.bufferFull = bufferFull; }
        
        public boolean isDiagnosticMode() { return diagnosticMode; }
        public void setDiagnosticMode(boolean diagnosticMode) { this.diagnosticMode = diagnosticMode; }
        
        public boolean isPaperFeed() { return paperFeed; }
        public void setPaperFeed(boolean paperFeed) { this.paperFeed = paperFeed; }
        
        public boolean isOnline() { return online; }
        public void setOnline(boolean online) { this.online = online; }

        @Override
        public String toString() {
            return "PrinterStatus{" +
                    "paperOut=" + paperOut +
                    ", pause=" + pause +
                    ", ribbonOut=" + ribbonOut +
                    ", online=" + online +
                    '}';
        }
    }
}
