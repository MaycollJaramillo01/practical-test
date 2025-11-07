package Entity

import android.graphics.Bitmap
import java.time.LocalDate

class Person {
    private var id: String=""
    private var name: String=""
    private var fLastName: String=""
    private var sLastName: String=""
    private var phone: Int=0
    private var email: String=""
    private var state: String=""
    private var district: String=""
    private var address: String=""


    constructor()

    constructor(id: String, name: String, flastname: String
            , slastname: String, phone: Int, email: String
            , birthday: LocalDate, state: String
            , district: String, address: String, latitude: Int
            , longitude: Int, photo: Bitmap?)
    {
        this.id=id
        this.name=name
        this.fLastName=flastname
        this.sLastName=slastname
        this.phone=phone
        this.email=email
        this.state=state
        this.district=district
        this.address=address
    }

    var ID: String
        get() = this.id
        set(value) {this.id=value}

    var Name: String
        get() = this.name
        set(value) {this.name=value}

    var FLastName: String
        get() = this.fLastName
        set(value) {this.fLastName=value}

    var SLastName: String
        get() = this.sLastName
        set(value) {this.sLastName=value}

    var Phone: Int
        get() = this.phone
        set(value) {this.phone=value}

    var Email: String
        get() = this.email
        set(value) {this.email=value}

    var State: String
        get() = this.state
        set(value) {this.state=value}

    var District: String
        get() = this.district
        set(value) {this.district=value}

    var Address: String
        get() = this.address
        set(value) {this.address=value}

    fun FullName() = "$this.name $this.fLastName $this.sLastName"
}