package cn.javaer.intellij.plugin

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import net.coobird.thumbnailator.Thumbnails
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.FileTime
import java.util.concurrent.TimeUnit
import javax.swing.Icon
import javax.swing.ImageIcon

/**
 * @author cn-src
 */
class ImageIconProvider : IconProvider() {
    private val cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build<String, Pair<FileTime, ImageIcon>>(CacheLoader.from { key ->
                val outputStream = ByteArrayOutputStream()
                val path = Paths.get(key)
                Files.newInputStream(path).use {
                    Thumbnails.of(it)
                            .size(16, 16)
                            .toOutputStream(outputStream)
                }

                return@from Pair(Files.getLastModifiedTime(path), ImageIcon(outputStream.toByteArray()))
            })

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
        val pair = cache.get(canonicalPath)
        if (Files.getLastModifiedTime(Paths.get(canonicalPath)) != pair.first) {
            cache.refresh(canonicalPath)
        }
        return cache.get(canonicalPath).second
    }
}