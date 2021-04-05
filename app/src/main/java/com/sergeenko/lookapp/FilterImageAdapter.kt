package com.sergeenko.lookapp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.imageprocessors.Filter
import java.io.File

class FilterImageAdapter(val height: Int) : RecyclerView.Adapter<FilterImageViewHolder>() {

     var canScroll: () -> Boolean = { true }
    var fileList = listOf<FilterImage>()
    var selectedCount = 0


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
                file.bitmapFiltered = filter?.processFilter(file.bitmap?.copy(Bitmap.Config.ARGB_8888, true))
                file.filter = filter
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
            file.hasCont
        }catch (e: Exception){
            false
        }
    }

    fun hasBackgroundChanges(currentPosition: Int): Boolean {
        return try {
            val file = fileList[currentPosition]
            file.hasBg
        }catch (e: Exception){
            false
        }
    }

    fun hasBrightnesChanges(currentPosition: Int): Boolean {
        return try {
            val file = fileList[currentPosition]
            file.hasBrightnes
        }catch (e: Exception){
            false
        }
    }

    fun saveBrightness(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.saveBrightness()
        }catch (e: Exception){

        }
    }

    fun saveContrast(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.saveContrast()
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
            file.hasBrightnes = false
            notifyItemChanged(currentPosition)
        }catch (e: Exception){

        }
    }

    fun clearContrast(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.hasCont = false
            notifyItemChanged(currentPosition)
        }catch (e: Exception){

        }
    }

    fun clearBackground(currentPosition: Int) {
        try {
            val file = fileList[currentPosition]
            file.hasBg = false
            notifyItemChanged(currentPosition)
        }catch (e: Exception){

        }
    }

    var onItemTouch = {it: Boolean->}

}

data class FilterImage(
    val file: File,
    var filter: Filter? = null,
    var bitmap: Bitmap? = null,
    var bitmapFiltered: Bitmap? = null,
    var scale: Float = 1f,
    var xCoord: Float = 0f,
    var yCoord: Float = 0f
) {

    var oldScale: Float = 1f
    var oldXCoord: Float = 0f
    var oldYCoord: Float = 0f

    var hasBrightnes = false
    var hasCont = false
    var hasBg = false

    fun clearOrientation(){
        scale = oldScale
        xCoord = oldXCoord
        yCoord = oldYCoord
    }

    fun saveOrientation(){
        oldScale = scale
        oldXCoord = xCoord
        oldYCoord = yCoord
    }

    fun hasOrientationChanges(): Boolean {
        return oldScale != 1f || oldXCoord != 0f || oldYCoord != 0f
    }

    fun saveBrightness() {
        hasBrightnes = true
    }

    fun saveContrast() {
        hasCont = true
    }

    fun saveBG() {
        hasBg = true
    }

}
