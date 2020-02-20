package mx.edu.ittepic.ladm_u1_practica3_ismaelcastaneda

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    val vector = Array< Int >( 10, { 0 })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        asignar.setOnClickListener {
            if(valor.text.isEmpty()  || posicion.text.isEmpty() ){
                AlertDialog.Builder(this)
                    .setTitle("Advertencia")
                    .setMessage("El campo valor y posicion no puede estar vacio")
                    .setPositiveButton("Ok") {d,i->}
                    .show()
                return@setOnClickListener
            }
            if(posicion.text.toString().toInt()<0 || posicion.text.toString().toInt()>9){
                AlertDialog.Builder(this)
                    .setTitle("Advertencia")
                    .setMessage("El campo posicion debe ser un número entre 0 y 9")
                    .setPositiveButton("Ok") {d,i->}
                    .show()
                return@setOnClickListener
            }
            vector.set(posicion.text.toString().toInt(), valor.text.toString().toInt())
            valor.setText("")
            posicion.setText("")
            AlertDialog.Builder(this)
                .setTitle("Exito")
                .setMessage("Se ha establecido un nuevo valor")
                .setPositiveButton("Ok") {d,i->}
                .show()
        }

        mostrar.setOnClickListener {
            var data = ""
            (0..9).forEach {
                data = data + "[ " + it + " ] : " + vector[it].toString() + "\n"
            }
            txtMostrar.setText(data)
        }

        guardar.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    0)
            }
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED) {
                if(nombreGuardar.text.isEmpty()){
                    AlertDialog.Builder(this)
                        .setTitle("Advertencia")
                        .setMessage("El campo nombre (para guardar) no puede estar vacio")
                        .setPositiveButton("Ok") {d,i->}
                        .show()
                    return@setOnClickListener
                }
                guardarExterna()
            }else{
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Necesitas dar permisos")
                    .setPositiveButton("Ok") {d,i->}
                    .show()
            }
            return@setOnClickListener
        }

        leer.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    0)
            }
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED) {
                if(nombreGuardar.text.isEmpty()){
                    AlertDialog.Builder(this)
                        .setTitle("Advertencia")
                        .setMessage("El campo nombre (para abrir) no puede estar vacio")
                        .setPositiveButton("Ok") {d,i->}
                        .show()
                    return@setOnClickListener
                }
                abrirExterna()
            }else{
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Necesitas dar permisos")
                    .setPositiveButton("Ok") {d,i->}
                    .show()
            }
            return@setOnClickListener
        }
    }

    fun noSD():Boolean{
        var estado = Environment.getExternalStorageState()
        if(estado != Environment.MEDIA_MOUNTED){
            return true
        }
        return false
    }

    fun guardarExterna() {
        if(noSD()){
            AlertDialog.Builder(this)
                .setTitle("Advertencia")
                .setMessage("No hay memoria externa")
                .setPositiveButton("Ok") {d,i->}
                .show()
            return
        }
        var rutaSD = Environment.getExternalStorageDirectory()
        var datosArchivo = File(rutaSD.absolutePath, nombreGuardar.text.toString())
        try {
            var flujoSalida = OutputStreamWriter(FileOutputStream(datosArchivo))
            var data = ""
            (0..9).forEach {
                if( it == 9){
                    data = data + vector[it].toString()
                }else{
                    data = data + vector[it].toString() + "&"
                }
            }
            flujoSalida.write(data)
            flujoSalida.flush()
            flujoSalida.close()
            AlertDialog.Builder(this)
                .setTitle("Exito")
                .setMessage("Se guardo correctamente")
                .setPositiveButton("Ok") {d,i->}
                .show()
        }catch ( error : IOException){
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error.message.toString())
                .setPositiveButton("Ok") {d,i->}
                .show()
        }
    }

    fun abrirExterna() {
        if(noSD()){
            AlertDialog.Builder(this)
                .setTitle("Advertencia")
                .setMessage("No hay memoria externa")
                .setPositiveButton("Ok") {d,i->}
                .show()
            return
        }
        var rutaSD = Environment.getExternalStorageDirectory()
        var datosArchivo = File(rutaSD.absolutePath, nombreAbrir.text.toString())
        try {
            var flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(datosArchivo)))
            var data = flujoEntrada.readLine()
            var dataSplit = data.split("&")
            (0..9).forEach {
                vector[it] = dataSplit[it].toInt()
            }
            AlertDialog.Builder(this)
                .setTitle("Exito")
                .setMessage("Los datos guardados se han establecido")
                .setPositiveButton("Ok") {d,i->}
                .show()
            flujoEntrada.close()
        }catch ( error : IOException ){
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error.message.toString())
                .setPositiveButton("Ok") {d,i->}
                .show()
        }
    }
}
