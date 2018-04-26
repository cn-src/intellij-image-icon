package cn.javaer.intellij.plugin

import net.coobird.thumbnailator.Thumbnails
import org.junit.Test
import java.io.ByteArrayOutputStream
import javax.swing.ImageIcon

/**
 * @author zhangpeng
 */
class ImageIconProviderTest {
    @Test
    fun name() {
        val outputStream = ByteArrayOutputStream()
        Thumbnails.of(this.javaClass.getResourceAsStream("/demo.png"))
                .size(16, 16)
                .toOutputStream(outputStream)
        val imageIcon = ImageIcon(outputStream.toByteArray())
    }
}