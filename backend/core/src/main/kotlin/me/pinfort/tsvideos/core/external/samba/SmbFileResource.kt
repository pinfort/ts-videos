package me.pinfort.tsvideos.core.external.samba

import jcifs.SmbResource
import org.springframework.core.io.AbstractResource
import java.io.InputStream

class SmbFileResource(
    private val smbResource: SmbResource,
    private val length: Long,
) : AbstractResource() {
    override fun getDescription(): String = "SMB resource [${smbResource.name}]"

    override fun getInputStream(): InputStream = smbResource.openInputStream().buffered()

    override fun contentLength(): Long = length

    override fun exists(): Boolean = true
}
