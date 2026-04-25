package com.example.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object PdfToBitmapConverter {

    private const val SCALE_FACTOR = 3.0f

    suspend fun convert(context: Context, uri: Uri): Result<List<Bitmap>> = withContext(Dispatchers.IO) {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return@withContext Result.failure(
                    Exception("Failed to open file descriptor")
                )
            parcelFileDescriptor.use { pfd ->
                val pdfRenderer = PdfRenderer(pfd)
                val pageCount = pdfRenderer.pageCount
                val bitmapList = mutableListOf<Bitmap>()

                for (pageIndex in 0 until pageCount) {
                    val page = pdfRenderer.openPage(pageIndex)
                    val scaledWidth = (page.width * SCALE_FACTOR).toInt()
                    val scaledHeight = (page.height * SCALE_FACTOR).toInt()

                    val bitmap = Bitmap.createBitmap(
                        scaledWidth,
                        scaledHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap.eraseColor(Color.WHITE)

                    page.render(
                        bitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_PRINT
                    )
                    val processedBitmap = preprocessBitmap(bitmap)

                    bitmapList.add(processedBitmap)
                    page.close()
                }
                pdfRenderer.close()
                Result.success(bitmapList)
            }

        } catch (e: IOException) {
            Result.failure(Exception("IO Error: ${e.message}", e))
        } catch (e: SecurityException) {
            Result.failure(Exception("Permission denied: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}", e))
        }
    }

    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        return bitmap
    }

    suspend fun convertWithDpi(context: Context, uri: Uri, dpi: Int = 300): Result<List<Bitmap>> = withContext(Dispatchers.IO) {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return@withContext Result.failure(
                    Exception("Failed to open file descriptor")
                )

            parcelFileDescriptor.use { pfd ->
                val pdfRenderer = PdfRenderer(pfd)
                val pageCount = pdfRenderer.pageCount
                val bitmapList = mutableListOf<Bitmap>()

                for (pageIndex in 0 until pageCount) {
                    val page = pdfRenderer.openPage(pageIndex)

                    val dpiScale = dpi / 72f
                    val scaledWidth = (page.width * dpiScale).toInt()
                    val scaledHeight = (page.height * dpiScale).toInt()

                    val bitmap = Bitmap.createBitmap(
                        scaledWidth,
                        scaledHeight,
                        Bitmap.Config.ARGB_8888
                    )

                    bitmap.eraseColor(Color.WHITE)

                    page.render(
                        bitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_PRINT
                    )

                    bitmapList.add(bitmap)
                    page.close()
                }

                pdfRenderer.close()
                Result.success(bitmapList)
            }

        } catch (e: Exception) {
            Result.failure(Exception("Conversion failed: ${e.message}", e))
        }
    }
}