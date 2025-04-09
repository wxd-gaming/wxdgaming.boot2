package code.qr;

import org.junit.Test;

import java.util.Date;

public class QRCodeBoxTest {

    @Test
    public void t0() throws Exception {
        /*二维码内容*/
        String content = "https://wan.ludashi.com/h5pay/index?sign=26a401818f015dbbf966cd4f0f25fadf&change=0";
        String logoPath = "D:\\logo.jpeg"; // 二维码中间的logo信息 非必须

        /*设置二维码矩阵的信息*/
        QRCodeBox.builder()
                .content(content)
                .width(500)
                .height(500)
                .reduceWhiteArea(22)
                // .addLogo(logoPath)
                .build()
                .writeToFile("target/" + new Date().getTime() + ".png");
    }

}
