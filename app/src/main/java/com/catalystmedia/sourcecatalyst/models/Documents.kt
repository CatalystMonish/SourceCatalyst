package com.catalystmedia.sourcecatalyst.models;

public class Documents {

    private var documentName:String = ""
    private var documentLink:String = ""
    private var locked:Boolean = false


    constructor()
    constructor(documentName: String, documentLink: String, locked: Boolean)
    {
        this.documentName = documentName
        this.documentLink = documentLink
        this.locked = locked

    }
    fun getdocumentName():String
    {
        return documentName
    }
    fun setdocumentName(documentName:String){
        this.documentName = documentName
    }
    fun getdocumentLink():String
    {
        return documentLink
    }
    fun setdocumentLink(documentLink:String){
        this.documentLink = documentLink
    }
    fun getlocked():Boolean
    {
        return locked
    }
    fun setlocked(locked:Boolean){
        this.locked = locked
    }

}
