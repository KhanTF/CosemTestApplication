package com.honeywell.cosemtestapplication.model.excel

import android.content.Context
import android.net.Uri
import fr.andrea.libcosemclient.common.HexConvert
import fr.andrea.libcosemclient.cosem.LogicalName
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileOutputStream

class ExcelLogicalName(val name: String, val version: Int, classId: Int, instanceId: ByteArray, attributeId: Byte) :
    LogicalName(classId, instanceId, attributeId)