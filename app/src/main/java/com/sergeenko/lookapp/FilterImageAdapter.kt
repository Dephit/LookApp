package com.sergeenko.lookapp

import android.graphics.*
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.imageprocessors.Filter
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.io.Serializable


class FilterImageAdapter(val height: Int) : RecyclerView.Adapter<FilterImageViewHolder>() {

    var canScroll: () -> Boolean = { true }
    var fileList = listOf<FilterImage>()
    var selectedCount = 0
    var rotationMode: RotationMode = RotationMode.None


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterImageViewHolder {
        return FilterImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.filter_image_view, null, false), height)
    }

    fun delete(currentId: Int){
        setList(fileList.filterIndexed { index, filterImage -> index != currentId })
    }

    override fun onBindViewHolder(holder: FilterImageViewHolder, position: Int) {
        holder.bind(
                file = fileList[position],
                canScroll = canScroll
        )
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun setList(_fileList: List<FilterImage>) {
        fileList = _fileList
        notifyDataSetChanged()
    }

    fun getCurrentFile(position: Int) = fileList[position]

    fun applyFilter(position: Int, filter: Filter?): Boolean {
        return try {
            val file = fileList[position]
            if(file.filter == filter){
                file.bitmapFiltered = null
                file.filter = null
            }else{
                file.filter = filter
                file.bitmapFiltered = filter?.processFilter(file.bitmap?.copy(Bitmap.Config.ARGB_8888, true))
            }
            notifyItemChanged(position)
            file.filter != null
        }catch (e: Exception){
            false
        }
    }

    fun clearOrientation(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.clearOrientation()
            notifyItemChanged(currentPosition)
        }catch (e: Exception){

        }
    }

    fun hasOrientationChanges(currentPosition: Int): Boolean {
        return try {
            val file = fileList[currentPosition]
            file.hasOrientationChanges()
        }catch (e: Exception){
            false
        }
    }

    fun saveOrientation(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.saveOrientation()
        }catch (e: Exception){

        }
    }

    fun hasContrastChanges(currentPosition: Int): Boolean {
        return try {
            val file = fileList[currentPosition]
            file.hasContrastChanges()
        }catch (e: Exception){
            false
        }
    }

    fun hasBackgroundChanges(currentPosition: Int): Boolean {
        return try {
            val file = fileList[currentPosition]
            file.hasBG()
        }catch (e: Exception){
            false
        }
    }

    fun hasBrightnesChanges(currentPosition: Int): Boolean {
        return try {
            fileList[currentPosition].hasBrightnessChanges()
        }catch (e: Exception){
            false
        }
    }

    fun saveBrightness(currentPosition: Int) {
        try {
            fileList[currentPosition].saveBrightness()
        }catch (e: Exception){

        }
    }

    fun saveContrast(currentPosition: Int) {
        try {
            fileList[currentPosition].saveContrast()
        }catch (e: Exception){

        }
    }

    fun saveBG(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.saveBG()
        }catch (e: Exception){

        }
    }

    fun clearBrightness(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.clearBrightness()
            notifyItemChanged(currentPosition)
        }catch (e: Exception){

        }
    }

    fun clearContrast(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.clearContrast()
            notifyItemChanged(currentPosition)
        }catch (e: Exception){

        }
    }

    fun clearBackground(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.clearBG()
            notifyItemChanged(currentPosition)
        }catch (e: Exception){

        }
    }

    suspend fun rotate(position: Int, i: Float): Float {
        return try {
            with(fileList[position]) {
                rotationX
                rotationX += i
                if (rotationX > 360) {
                    rotationX = 0f
                } else if (rotationX < 0) {
                    rotationX = 360f
                }
                rotateFlow.emit(rotationX)
                rotationX
            }
        }catch (e: Exception){
            0f
        }
    }

    suspend fun rotateY(position: Int, i: Float): Float {
        return try {
            with(fileList[position]) {
                rotationY
                rotationY += i
                if (rotationY > 360) {
                    rotationY = 0f
                } else if (rotationY < 0) {
                    rotationY = 360f
                }
                rotateFlow.emit(rotationY)
                rotationY
            }
        }catch (e: Exception){
            0f
        }
    }

    suspend fun rotateZ(position: Int, i: Float): Float {
        return try {
            with(fileList[position]) {
                rotationZ
                rotationZ += i
                if (rotationZ > 360) {
                    rotationZ = 0f
                } else if (rotationZ < 0) {
                    rotationZ = 360f
                }
                rotateFlow.emit(rotationZ)
                rotationZ
            }
        }catch (e: Exception){
            0f
        }
    }

    suspend fun addBrightness(currentPosition: Int, value: Int) {
        try {
            with(fileList[currentPosition]) {
                brightness = value
                rotateFlow.emit(value.toFloat())
            }
        }catch (e: Exception){
            0f
        }
    }

    suspend fun addContrast(currentPosition: Int, value: Float) {
        try {
            with(fileList[currentPosition]) {
                contrast = value
                rotateFlow.emit(value)
            }
        }catch (e: Exception){
            0f
        }
    }

    suspend fun setBackground(currentPosition: Int, color: String) {
        try {
            with(fileList[currentPosition]) {
                backgroundColor = color
                notifyItemChanged(currentPosition)
            }
        }catch (e: Exception){
            0f
        }
    }
}

data class FilterImage(
        val file: Uri,
        var filter: Filter? = null,
        var bitmap: Bitmap? = null,
        var bitmapFiltered: Bitmap? = null,
        var scale: Float = 1f,
        var xCoord: Float = 0f,
        var yCoord: Float = 0f,
        var view: View? = null
) {

    var rotationX: Float = 0f
    var oldRotationX: Float = 0f

    var rotationY: Float = 0f
    var oldRotationY: Float = 0f

    var rotationZ: Float = 0f
    var oldRotationZ: Float = 0f

    val rotateFlow: MutableStateFlow<Float> = MutableStateFlow(oldRotationZ)

    var backgroundColor: String? = null
    var oldBackgroundColor: String = "#C4C4C4"

    var oldScale: Float = 1f
    var oldXCoord: Float = 0f
    var oldYCoord: Float = 0f

    var oldBrightness: Int = 0
    var brightness: Int = 0
        //set(value) = if(value > 99) field = 99 else field = value

    var oldContrast: Float = 1f
    var contrast: Float = 1f
    var hasBg = false

    fun clearOrientation(){
        rotateFlow.value = oldRotationX
        rotationX = oldRotationX
        rotationY = oldRotationY
        rotationZ = oldRotationZ

        scale = oldScale
        xCoord = oldXCoord
        yCoord = oldYCoord
    }

    fun clearBG(){
        backgroundColor = oldBackgroundColor
    }

    fun saveOrientation(){
        oldRotationX = rotationX
        oldRotationZ = rotationZ
        oldRotationY = rotationY
        oldScale = scale
        oldXCoord = xCoord
        oldYCoord = yCoord
    }

    fun hasBG(): Boolean{
        return oldBackgroundColor != "#C4C4C4"
    }

    fun hasOrientationChanges(): Boolean {
        return oldScale != 1f || oldXCoord != 0f || oldYCoord != 0f || oldRotationX != 0f || oldRotationY != 0f || oldRotationZ != 0f
    }

    fun hasBrightnessChanges(): Boolean {
        return oldBrightness != 0
    }

    fun saveBG() {
        oldBackgroundColor = backgroundColor.toString()
    }

    fun saveBrightness() {
        oldBrightness = brightness
    }

    fun saveContrast() {
        oldContrast = contrast
    }


    fun setBrightness( contrast: Float, brightness: Float): ColorMatrixColorFilter {
        val cm = ColorMatrix(floatArrayOf(contrast, 0f, 0f, 0f, brightness, 0f, contrast, 0f, 0f, brightness, 0f, 0f, contrast, 0f, brightness, 0f, 0f, 0f, 1f, 0f))
        return ColorMatrixColorFilter(cm)
    }



    fun clearBrightness() {
        brightness = oldBrightness
        rotateFlow.value = oldRotationX
    }

    fun hasContrastChanges(): Boolean {
        return oldContrast != 1f
    }

    fun clearContrast() {
        contrast = oldContrast
        rotateFlow.value = oldRotationX
    }

}
