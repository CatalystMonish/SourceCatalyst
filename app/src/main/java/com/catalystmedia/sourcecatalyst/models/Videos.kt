package com.catalystmedia.sourcecatalyst.models;

public class Videos {

    private var videoName:String = ""
    private var videoLink:String = ""
    private var locked:Boolean = false


    constructor()
    constructor(videoName: String, videoLink: String, locked: Boolean)
    {
        this.videoName = videoName
        this.videoLink = videoLink
        this.locked = locked

    }
    fun getvideoName():String
    {
        return videoName
    }
    fun setvideoName(videoName:String){
        this.videoName = videoName
    }
    fun getvideoLink():String
    {
        return videoLink
    }
    fun setvideoLink(videoLink:String){
        this.videoLink = videoLink
    }
    fun getlocked():Boolean
    {
        return locked
    }
    fun setlocked(locked:Boolean){
        this.locked = locked
    }

}
