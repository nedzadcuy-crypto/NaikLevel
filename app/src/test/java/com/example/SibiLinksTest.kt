package com.example

import com.example.data.sibi.SibiLinks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Robolectric dipakai karena SibiLinks memakai android.net.Uri.
@RunWith(RobolectricTestRunner::class)
class SibiLinksTest {
    @Test fun catalogUrlAllowed() = assertTrue(SibiLinks.isAllowedSourceUrl(SibiLinks.CATALOG_URL))
    @Test fun httpRejected() = assertFalse(SibiLinks.isAllowedSourceUrl("http://buku.kemendikdasmen.go.id/katalog/"))
    @Test fun otherHostRejected() = assertFalse(SibiLinks.isAllowedSourceUrl("https://evil.example.com/katalog/"))
    @Test fun lookalikeHostRejected() =
        assertFalse(SibiLinks.isAllowedSourceUrl("https://buku.kemendikdasmen.go.id.evil.com/"))
    @Test fun nullRejected() = assertFalse(SibiLinks.isAllowedSourceUrl(null))
    @Test fun invalidFallsBackToCatalog() = assertEquals(SibiLinks.CATALOG_URL, SibiLinks.safeSourceUrlOrCatalog("javascript:alert(1)"))
}
