package org.iesharia.myapplication

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.iesharia.myapplication.ui.theme.MyApplicationTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) { innerPadding ->
                    MainActivity(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@SuppressLint("Range", "RememberReturnType")
@Composable
fun MainActivity(modifier: Modifier) {
    val context = LocalContext.current
    val db = DBHelper(context)

    var lName = remember { mutableStateListOf<String>() }
    var lAge = remember { mutableStateListOf<String>() }
    var lId = remember { mutableStateListOf<String>() }
    var selectedItems = remember { mutableStateListOf<Int>() } // Lista de índices seleccionados


    fun mostrar(){
        try {
            val cursor = db.getName()
            cursor?.let {
                lName.clear()
                lAge.clear()
                lId.clear()
                selectedItems.clear()  // Reiniciar selección

                cursor.moveToFirst()
                do {
                    lName.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)))
                    lAge.add(cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)))
                    lId.add(cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL)))
                } while (cursor.moveToNext())

                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Base de Datos",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Text(
            text = "Muuuuuy simple\nNombre/Edad",
            fontSize = 10.sp
        )

        // Campo de texto para el nombre
        var nameValue by remember { mutableStateOf("") }
        OutlinedTextField(
            value = nameValue,
            onValueChange = { nameValue = it },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Nombre") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        // Campo de texto para la edad
        var ageValue by remember { mutableStateOf("") }
        OutlinedTextField(
            value = ageValue,
            onValueChange = { ageValue = it },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Edad") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Row {
            // Botón "Añadir"
            Button(
                modifier = Modifier.padding(15.dp),
                onClick = {
                    val name = nameValue
                    val age = ageValue
                    db.addName(name, age)
                    Toast.makeText(context, "$name adjuntado a la base de datos", Toast.LENGTH_LONG).show()

                    nameValue = ""
                    ageValue = ""

                    mostrar()
                }
            ) {
                Text(text = "Añadir")
            }



            // Botón "Borrar Seleccionados"
            Button(
                modifier = Modifier.padding(15.dp),
                onClick = {
                    // Eliminar todos los elementos seleccionados
                    for (i in selectedItems.sortedDescending()) {
                        val wasDeleted = db.deleteName(lId[i])
                        if (wasDeleted) {
                            lName.removeAt(i)
                            lAge.removeAt(i)
                            lId.removeAt(i)
                        }
                    }
                    selectedItems.clear()
                    Toast.makeText(context, "Registros seleccionados eliminados", Toast.LENGTH_SHORT).show()
                    mostrar()
                }
            ) {
                Text(text = "Borrar")
            }
            // boton para actualizar
            Button(
                modifier = Modifier.padding(15.dp),
                onClick = {
                    for (i in selectedItems) {
                        val id = lId[i]  // Obtener el ID del elemento seleccionado
                        val edited = db.updateName(id, nameValue, ageValue)  // Actualizar el nombre y la edad en la base de datos
                        if (edited) {
                            lName[i] = nameValue  // Actualizar el nombre en la lista local
                            lAge[i] = ageValue    // Actualizar la edad en la lista local
                        }
                    }
                    selectedItems.clear()  // Limpiar los elementos seleccionados después de actualizar
                    Toast.makeText(context, "Registros seleccionados actualizados", Toast.LENGTH_SHORT).show()
                    mostrar()
                    }

            ) {
                Text(text = "Actu")
            }
        }

        // Lista de nombres mostrados
        LazyColumn(
            modifier = Modifier.padding(20.dp)
        ) {
            itemsIndexed(lName) { i, name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            if (i in selectedItems) {
                                selectedItems.remove(i)  // Deseleccionar si ya estaba seleccionado
                            } else {
                                selectedItems.add(i)    // Seleccionar si no estaba seleccionado
                            }
                        }


                ) {
                    Text(
                        text = name,
                        modifier = Modifier.padding(20.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lAge[i],
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}
