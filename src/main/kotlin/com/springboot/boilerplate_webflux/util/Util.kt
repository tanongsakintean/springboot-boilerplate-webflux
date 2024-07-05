package com.springboot.boilerplate_webflux.util


import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.InputStream
import java.io.OutputStream
import java.math.BigDecimal
import java.sql.Timestamp
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec




fun getGmtDateTime(): Date {
    val nowUtc = Instant.now()
    val gmtZone = ZoneId.of("GMT")
    val nowAsiaThai = ZonedDateTime.ofInstant(nowUtc, gmtZone)
    val localDateTime = nowAsiaThai.toLocalDateTime()

    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar[localDateTime.year, localDateTime.monthValue - 1, localDateTime.dayOfMonth, localDateTime.hour, localDateTime.minute] =
        localDateTime.second

    return calendar.time
}

fun convertGmtToDate(timestamp: Timestamp?): String {
    if (timestamp == null) {
//        throw IllegalArgumentException("Timestamp cannot be null")
        return ""
    }

    val gmtZone = ZoneId.of("Asia/Bangkok")
    val instant = timestamp.toInstant()
    val zonedDateTime = ZonedDateTime.ofInstant(instant, gmtZone)

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:MM")
    return formatter.format(zonedDateTime)
}


fun convertGmtToDateNoTime(timestamp: Timestamp?): String {
    if (timestamp == null) {
//        throw IllegalArgumentException("Timestamp cannot be null")
        return ""
    }

    val gmtZone = ZoneId.of("Asia/Bangkok")
    val instant = timestamp.toInstant()
    val zonedDateTime = ZonedDateTime.ofInstant(instant, gmtZone)

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return formatter.format(zonedDateTime)
}


fun convertNumberFormat(money : String) : String{
    val priceString = money
    val priceNumber = priceString.toDoubleOrNull() ?: 0.0 // แปลงเป็น Double หรือใช้ค่าเริ่มต้น 0.0 ถ้าไม่สามารถแปลงได้

    val numberFormat = NumberFormat.getNumberInstance(Locale.US) // เลือก locale เพื่อให้ค่าที่แสดงมี comma ตามที่ต้องการ
    val formattedPrice = numberFormat.format(priceNumber)
    return formattedPrice
}


fun calculateEncodeHMAC(data: String, key: String): String? {
    val signingKey = SecretKeySpec(key.toByteArray(), "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(signingKey)

    val rawHmac = mac.doFinal(data.toByteArray())
    val encodedString = Base64.getEncoder().encodeToString(rawHmac)

    return encodedString
}

fun idToShortURL(): String? {
    // Map to store 62 possible characters
    val currentTime = System.currentTimeMillis()
    var n = currentTime
    val map = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray()
    val shortUrl = StringBuffer()

    // Convert given integer id to a base 62 number
    while (n > 0) {
        // use above map to store actual character
        // in short url
        shortUrl.append(map[(n % 62).toInt()])
        n = n / 62
    }

    // Reverse shortURL to complete base conversion
    return shortUrl.reverse().toString()
}

fun isEmailValid(email: String): Boolean {
    return Pattern.compile(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    ).matcher(email).matches()
}

fun convert(strNum: String): String? {
    var strNum = strNum
    val parts: Array<String> // parts[0]-> beforeDecimal, parts[1]-> after decimal
    if (strNum.length != 0) {
        // delete  commas,semicolon and white spaces .
        strNum = strNum.replace("[,\\s]".toRegex(), "") //	 strNum.replaceAll("[+\\-,\\s]","");
        //delete leading plus and minus sign
        val firstChar = strNum[0]
        val lastChar = strNum[strNum.length - 1]
        if (firstChar == '+' || firstChar == '-' || firstChar == '฿') strNum = strNum.substring(1)
        if (lastChar == '฿') strNum = strNum.substring(0, strNum.length - 1)
    } else return "ศูนย์บาทถ้วน"
    return if (isConvertible(strNum)) {
        // round number to 2 decimal point using BigDecimal for precision and accuracy
        var bdNum = BigDecimal(strNum)
        bdNum = bdNum.setScale(2, BigDecimal.ROUND_HALF_EVEN) // setScale = set number of decimal point to 2
        // 0.004 -> 0.00 After rounding,  a number may  becomes to zero.
        if (bdNum.compareTo(BigDecimal.ZERO) == 0) return "ศูนย์บาทถ้วน"
        // 123.998 -> 124.00 Number becomes only integer.
        var isOnlyInteger = false
        var suffix = "บาท"
        if (bdNum.stripTrailingZeros().scale() <= 0) { //  equivalent to  (dblNum % 1 == 0)
            isOnlyInteger = true
            suffix = "บาทถ้วน"
        }
        strNum = bdNum.toPlainString()
        parts = strNum.split(".").dropLastWhile { it.isEmpty() }.toTypedArray()
        val bdPart0 = BigDecimal(parts[0])
        val strBahtText = StringBuilder()
        //		String strBahtText="";
        if (bdPart0.compareTo(BigDecimal.ZERO) != 0) {
            val numberLength = parts[0].length
            val groupedNumber = ArrayList<String>()
            var numGroup: Int
            val remainder: Int
            var m = 0
            if (numberLength > 7) {
                numGroup = numberLength / 6
                remainder = numberLength % 6
                // divide  'parts[0]' equally into 'numGroup' group
                if (remainder == 0) {
                    var i = 0
                    while (i < numberLength) {
                        groupedNumber.add(m, parts[0].substring(i, i + 6))
                        m = m + 1
                        i = i + 6
                    }
                } else { //divide 'parts[0] in different size
                    numGroup += 1 // numGroup from integer division plus the rest.
                    groupedNumber.add(m, parts[0].substring(0, remainder))
                    m = m + 1
                    var i = remainder
                    while (i < numberLength) {
                        groupedNumber.add(m, parts[0].substring(i, i + 6))
                        m = m + 1
                        i = i + 6
                    }
                }
                for (i in numGroup - 1 downTo 0) {
                    //				strBahtText =  (changeNumber2Word(groupedNumber.get(i), suffix)).concat(strBahtText);
                    strBahtText.insert(0, changeNumber2Word(groupedNumber[i], suffix))
                    suffix = "ล้าน"
                }
            } else  {
                strBahtText.append(changeNumber2Word(parts[0], suffix))
            }
        }
        if (!isOnlyInteger) {
            suffix = "สตางค์"
            //	strBahtText = strBahtText + " " + changeNumber2Word(parts[1],suffix);
            strBahtText.append(" ").append(changeNumber2Word(parts[1], suffix))
        }
        strBahtText.toString()
    } else "ไม่ใช่จำนวนเลข" //"NaN - Not a Number";
}

private fun isConvertible(strNum: String): Boolean {
    var dotCount = 0
    // check if there are characters or unwanted signs in strNum
    for (i in 0 until strNum.length) {
        val c = strNum[i]
        if (c != '.' && (c < '0' || c > '9')) return false
        // check if there are many dots. eg. "127.0.0.1" or only one dot, eg "."
        if (c == '.') dotCount = dotCount + 1
    }
    //	if (dotCount > 1 ||(dotCount==1 && strNum.length()==1)) return false;
    return dotCount <= 1 && (dotCount != 1 || strNum.length != 1)
}

private fun changeNumber2Word(strValue: String, lastWord: String): String? {
    val weight = arrayOf("", "สิบ", "ร้อย", "พัน", "หมื่น", "แสน", "ล้าน") // Digit weight
    val pn = arrayOf("", "หนึ่ง", "สอง", "สาม", "สี่", "ห้า", "หก", "เจ็ด", "แปด", "เก้า") // Digit pronounce
    val result = StringBuilder()
    val length = strValue.length
    println(strValue + lastWord)
    for (i in 0 until length) {
        val c = strValue[i]
        var n = c.toString().toInt()
        if (n > 0) {
            when (n) {
                1 -> if (i == length - 1 && length > 1) {
                    if (lastWord == "สตางค์" && strValue[length - 2] == '0') // case -> 0.01
                        result.append(pn[n]).append(weight[length - i - 1]) else result.append("เอ็ด")
                        .append(weight[length - i - 1])
                } else if (i == length - 2) {
                    result.append(weight[length - i - 1])
                } else result.append(pn[n]).append(weight[length - i - 1])
                2 -> if (i == length - 2) {
                    result.append("ยี่").append(weight[length - i - 1])
                } else result.append(pn[n]).append(weight[length - i - 1])
                else -> result.append(pn[n]).append(weight[length - i - 1])
            }
        }
    }
    return result.toString() + lastWord
}
