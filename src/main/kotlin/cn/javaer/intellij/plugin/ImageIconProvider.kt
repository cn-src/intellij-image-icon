package cn.javaer.intellij.plugin

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import net.coobird.thumbnailator.Thumbnails
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.Icon
import javax.swing.ImageIcon

/**
 * @author zhangpeng
 */
class ImageIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val psiFile = element.containingFile
        if (psiFile?.virtualFile?.canonicalFile?.canonicalPath == null) {
            return null
        }
        val canonicalPath = psiFile.virtualFile.canonicalFile!!.canonicalPath!!
        if (canonicalPath.contains(".jar")) {
            return null
        }
        if (!(psiFile.name.endsWith(".jpeg", true)
              || psiFile.name.endsWith(".jpg", true)
              || psiFile.name.endsWith(".png", true)
              || psiFile.name.endsWith(".gif", true)
              || psiFile.name.endsWith(".bmp", true)
              || psiFile.name.endsWith(".WBMP", true))) {
            return null
        }
        val outputStream = ByteArrayOutputStream()
        Files.newInputStream(Paths.get(canonicalPath)).use {
            Thumbnails.of(it)
                    .size(16, 16)
                    .toOutputStream(outputStream)
        }

        return ImageIcon(outputStream.toByteArray())
    }
}