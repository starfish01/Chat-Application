package au.com.patricklabes.patricksmessenger.models

class ChatMessage(val id: String, val text: String, val fromId: String, val toId:String, val timestap:Long){
    constructor() : this("","","","",-1)
}