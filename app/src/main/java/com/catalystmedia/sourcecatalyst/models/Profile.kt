package com.catalystmedia.sourcecatalyst.models;

public class Profile {

    private var courseID:String = ""
    private var certificateLink:String = ""
    private var completionStatus:String = ""
    private var startDate:String = ""

    constructor()
    constructor(courseID: String, certificateLink: String, completionStatus: String, startDate: String)
    {
        this.courseID = courseID
        this.certificateLink = certificateLink
        this.completionStatus = completionStatus
        this.startDate = startDate

    }
    fun getCourseId():String
    {
        return courseID
    }
    fun setCourseId(courseID:String){
        this.courseID = courseID
    }
    fun getCertificateLink():String
    {
        return certificateLink
    }
    fun setCertificateLink(certificateLink:String){
        this.certificateLink = certificateLink
    }
    fun getCompletionStatus():String
    {
        return completionStatus
    }
    fun setCompletionStatus(completionStatus:String){
        this.completionStatus = completionStatus
    }
    fun getStartDate():String
    {
        return startDate
    }
    fun setStartDate(startDate:String){
        this.startDate = startDate
    }

}
