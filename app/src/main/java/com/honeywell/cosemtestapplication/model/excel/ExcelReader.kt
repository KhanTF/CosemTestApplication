package com.honeywell.cosemtestapplication.model.excel

import android.content.Context
import android.net.Uri
import fr.andrea.libcosemclient.common.HexConvert
import fr.andrea.libcosemclient.cosem.LogicalName
import io.reactivex.Observable
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileOutputStream

data class ExcelCosemObject(val name: String, val obis: String, val classId: Int, val version: Int)

class ExcelReader(private val context: Context) {

    companion object {
        private const val COLUMN_OBIS = 0
        private const val COLUMN_NAME = 3
        private const val COLUMN_CLASS_ID = 4
        private const val COLUMN_VERSION = 5
        private val OBIS_REGEX = Regex("\\d{1,3}-\\d{1,3}:\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}")
    }

    fun getExcelLogicalNames(uri: Uri): Observable<ExcelLogicalName> {
        return Observable.create { e ->
            val workbook = WorkbookFactory.create(context.contentResolver.openInputStream(uri))
            for (row in workbook.getSheetAt(0).rowIterator()) {
                val obisCell = row.getCell(COLUMN_OBIS) ?: continue
                val nameCell = row.getCell(COLUMN_NAME) ?: continue
                val classIdCell = row.getCell(COLUMN_CLASS_ID) ?: continue
                val versionCell = row.getCell(COLUMN_VERSION) ?: continue
                val splitRegex = Regex("\\D")
                if (versionCell.cellTypeEnum == CellType.NUMERIC && obisCell.cellTypeEnum == CellType.STRING && nameCell.cellTypeEnum == CellType.STRING && classIdCell.cellTypeEnum == CellType.NUMERIC) {
                    val obis = obisCell.stringCellValue.trim()
                    if (OBIS_REGEX.matches(obis)) {
                        val obisCode = obis
                            .split(splitRegex)
                            .map { it.toInt() }
                            .map { it.toByte() }
                            .toByteArray()
                        val version = versionCell.numericCellValue.toInt()
                        val classId = classIdCell.numericCellValue.toInt()
                        val name = nameCell.stringCellValue.trim()
                        val attributes = getClassIdGetAttributes(classId, version)
                        for (attribute in attributes) {
                            e.onNext(
                                ExcelLogicalName(
                                    name,
                                    version,
                                    classId,
                                    obisCode,
                                    attribute.toByte()
                                )
                            )
                        }
                    }
                }
            }
            e.onComplete()
        }
    }

    fun getClassIdGetAttributes(classId: Int, version: Int): IntRange {
        return when (version) {
            0 -> when (classId) {
                1 -> IntRange(1, 2)//
                3 -> IntRange(1, 3)//
                4 -> IntRange(1, 5)//
                5 -> IntRange(1, 9)//
                6 -> IntRange(1, 4)//
                7 -> IntRange(1, 8)//
                8 -> IntRange(1, 9)//
                9 -> IntRange(1, 2) //
                10 -> IntRange(1, 2) //
                11 -> IntRange(1, 2) //
                12 -> IntRange(1, 6)//
                17 -> IntRange(1, 2) //
                18 -> IntRange(1, 7)//
                20 -> IntRange(1, 10)//
                21 -> IntRange(1, 4)//
                22 -> IntRange(1, 4)//
                23 -> IntRange(1, 9) //
                25 -> IntRange(1, 5) //
                30 -> IntRange(1, 6)//
                40 -> IntRange(1, 7)//
                64 -> IntRange(1, 6)//
                else -> throw Exception("Not supported classId = ${classId}, version = $version")
            }
            1 -> when (classId) {
                7 -> IntRange(1, 8) //
                15 -> IntRange(1, 9)//
                23 -> IntRange(1, 9) //
                64 -> IntRange(1, 5)//
                else -> throw Exception("Not supported classId = ${classId}, version = $version")
            }
            3 -> when (classId) {
                15 -> IntRange(1, 9)
                else -> throw Exception("Not supported classId = ${classId}, version = $version")
            }
            else -> throw Exception("Not supported classId = ${classId}, version = $version")
        }
    }
}