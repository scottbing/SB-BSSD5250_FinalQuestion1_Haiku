@file:Suppress("PrivatePropertyName")

package edu.nmhu.bssd5250.haiku

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


@Suppress("LocalVariableName")
class MainActivity : AppCompatActivity() {
    private var word1: EditText? = null
    private var word2: EditText? = null
    private var word3: EditText? = null
    private var haiku: TextView? = null
    private var output: String? = null

    //private Bundle b = new Bundle();
    // establish syllable arrays
    private val one_syllable: ArrayList<String> = ArrayList()
    private val two_syllable: ArrayList<String> = ArrayList()
    private val three_syllable: ArrayList<String> = ArrayList()
    private val four_syllable: ArrayList<String> = ArrayList()
    private val five_syllable: ArrayList<String> = ArrayList()
    private val six_syllable: ArrayList<String> = ArrayList()
    private val seven_syllable: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect XML to code
        word1 = findViewById<View>(R.id.word1) as EditText
        word2 = findViewById<View>(R.id.word2) as EditText
        word3 = findViewById<View>(R.id.word3) as EditText
        haiku = findViewById<View>(R.id.haiku_text) as TextView
        val genHaiku = findViewById<View>(R.id.haiku) as Button
        genHaiku.setOnClickListener {
            Log.d("Gen Haiku", "Generate Haiku")
            processWords()
        }
    }

    private fun processWords() {

        // get user input fro each of the words
        val word01: String = word1!!.text.toString()
        if (word1!!.text.toString().trim().isEmpty()) word1!!.error = "Enter a word."
        startSendHttpRequestThread(word01, '1')

        val word02: String = word2!!.text.toString()
        if (word2!!.text.toString().trim().isEmpty()) word2!!.error = "Enter a word."
        startSendHttpRequestThread(word02, '2')

        val word03: String = word3!!.text.toString()
        if (word3!!.text.toString().trim().isEmpty()) word3!!.error = "Enter a word."
        startSendHttpRequestThread(word03, '3')

        // use the words to make a Haiku
        makeHaiku()

    }

    //Start a thread to send http request to web server use HttpURLConnection object.
    private fun startSendHttpRequestThread(word: String, whichWord: Char) {
        val sendHttpRequestThread = Thread {

            //make empty URL and connection
            val url: URL
            var ur1Connection: HttpURLConnection? =
                null //HttpsURLConnection
            try {
                val service = "https://rhymebrain.com/talk?" // call rhymebrain
                //String parm = "getRhymes&word=" + word;
                //String queryString = URLEncoder.encode(parm, "UTF-8");
                val queryString =
                    "getRhymes&word=$word&maxResults=$MAX_RESULTS"
                //try to process url and connect to it
                url = URL(service + "function=" + queryString)
                ur1Connection = url.openConnection() as HttpURLConnection
                ur1Connection.requestMethod = "GET"

                // Set connection timeout and read timeout value.
                ur1Connection.connectTimeout = 70000
                ur1Connection.readTimeout = 70000

                //create an input stream and stream reader from the connection
                val inputStream = ur1Connection.inputStream
                val inputStreamReader =
                    InputStreamReader(inputStream)

                //get some data from the stream
                var data = inputStreamReader.read()
                //string for collecting all output
                output = ""
                //if the stream is not empty
                while (data != -1) {
                    //turn what we read into a char and print it
                    val current = data.toChar()
                    output += current
                    data = inputStreamReader.read()

                    //Log.d("Network", output);
                }
                Log.d("Network", output!!)
                Log.i("Which", whichWord.toString())
                parseJSON(output!!)
                //parseJSON(output!!, whichWord)
            } catch (e: Exception) {
                Log.d("Network", e.toString())
            } finally {
                ur1Connection?.disconnect()
            }
        }
        // Start the child thread to request web page.
        sendHttpRequestThread.start()
    }

    private fun parseJSON(rhymeJSON: String) {
        try {
            var word: String
            var numOfSyllables: String

            // process JSON rhyming word list
            val jsonArray = JSONArray(rhymeJSON)
            for (i in 0 until jsonArray.length()) {
                val wordListObject = jsonArray.getJSONObject(i)
                word = wordListObject.getString("word")
                numOfSyllables = wordListObject.getString("syllables")
                when (numOfSyllables) {
                    "1" -> one_syllable.add(word)
                    "2" -> two_syllable.add(word)
                    "3" -> three_syllable.add(word)
                    "4" -> four_syllable.add(word)
                    else -> {}
                }
            }
        } catch (e: JSONException) {
            Log.d("MainActivity", e.toString())
        }
    }

    private fun makeHaiku() {

        // which syllable count has words
        val random = Random()
        val hasWords = booleanArrayOf(false, false, false, false, false, false, false)
        /*char[][] fiveSyllablePatterns =  {{2,3},{3,2},{1,4},{4,1}};
        char[][] sevenSyllablePatterns =  {{3,4},{4,3},{2,5},{5,2}};*/


        // check if arrays are populated
        if (one_syllable.size > 0) {
            hasWords[0] = true
        } else {
            // just in case the service is unavailable
            one_syllable.add("one")
            one_syllable.add("two")
            one_syllable.add("go")
            one_syllable.add("though")
            one_syllable.add("dough")
        }
        if (two_syllable.size > 0) {
            hasWords[1] = true
        } else {
            // just in case the service is unavailable
            two_syllable.add("chateaux")
            two_syllable.add("although")
            two_syllable.add("overgrow")
            two_syllable.add("idle")
            two_syllable.add("primal")
        }
        if (three_syllable.size > 0) {
            hasWords[2] = true
        } else {
            // just in case the service is unavailable
            three_syllable.add("buffalo")
            three_syllable.add("overflow")
            three_syllable.add("overthrow")
            three_syllable.add("sanity")
            three_syllable.add("balcony")
        }
        if (four_syllable.size > 0) {
            hasWords[3] = true
        } else {
            // just in case the service is unavailable
            four_syllable.add("portfolio")
            four_syllable.add("presidio")
            four_syllable.add("moustachio")
            four_syllable.add("catastrophe")
            four_syllable.add("totality")
        }

        Log.i("one_syllable.size: ", one_syllable.size.toString())
        Log.i("two_syllable.size: ", two_syllable.size.toString())
        Log.i("three_syllable.size: ", three_syllable.size.toString())
        Log.i("four_syllable.size: ", four_syllable.size.toString())

        // Randomly choose Haiku pattern
        // line 1: 5 syllables
        // line 2: 7 syllables
        // line 3. 5 syllables
        var stanza: String
        when (random.nextInt(5 - 1 + 1)) {
            1 -> {
                stanza = """${two_syllable[random.nextInt(two_syllable.size - 1)]} ${three_syllable[random.nextInt(three_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    """$stanza${three_syllable[random.nextInt(three_syllable.size - 1)]} ${four_syllable[random.nextInt(four_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    stanza + three_syllable[random.nextInt(three_syllable.size - 1)] + " " + two_syllable[random.nextInt(two_syllable.size - 1)] + "."
                haiku!!.text = stanza
            }
            2 -> {
                stanza = """
                    ${one_syllable[random.nextInt(one_syllable.size - 1)]} ${two_syllable[random.nextInt(two_syllable.size - 1)]} ${one_syllable[random.nextInt(one_syllable.size - 1)]} ${one_syllable[random.nextInt(one_syllable.size - 1)]},
                    
                    """.trimIndent()
                stanza =
                    """$stanza${three_syllable[random.nextInt(three_syllable.size - 1)]} ${four_syllable[random.nextInt(four_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    stanza + four_syllable[random.nextInt(four_syllable.size - 1)] + " " + one_syllable[random.nextInt(one_syllable.size - 1)] + "."
                haiku!!.text = stanza
            }
            3 -> {
                stanza = """${three_syllable[random.nextInt(three_syllable.size - 1)]} ${two_syllable[random.nextInt(two_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    """$stanza${one_syllable[random.nextInt(one_syllable.size - 1)]} ${three_syllable[random.nextInt(three_syllable.size - 1)]} ${three_syllable[random.nextInt(three_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    stanza + two_syllable[random.nextInt(two_syllable.size - 1)] + " " + three_syllable[random.nextInt(three_syllable.size - 1)] + "."
                haiku!!.text = stanza
            }
            4 -> {
                stanza = """${four_syllable[random.nextInt(four_syllable.size - 1)]} ${one_syllable[random.nextInt(one_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    """$stanza${three_syllable[random.nextInt(three_syllable.size - 1)]} ${two_syllable[random.nextInt(two_syllable.size - 1)]} ${two_syllable[random.nextInt(two_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    stanza + two_syllable[random.nextInt(two_syllable.size - 1)] + " " + three_syllable[random.nextInt(three_syllable.size - 1)]+ "."
                haiku!!.text = stanza
            }
            5 -> {
                stanza = """${three_syllable[random.nextInt(three_syllable.size - 1)]} ${two_syllable[random.nextInt(two_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    """$stanza${four_syllable[random.nextInt(four_syllable.size - 1)]} ${three_syllable[random.nextInt(three_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    stanza + one_syllable[random.nextInt(one_syllable.size - 1)] + " " + three_syllable[random.nextInt(three_syllable.size - 1)] + " " + one_syllable[random.nextInt(one_syllable.size - 1)] + "."
                haiku!!.text = stanza
            }
            else -> {
                stanza = """${two_syllable[random.nextInt(two_syllable.size - 1)]} ${three_syllable[random.nextInt(three_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    """$stanza${three_syllable[random.nextInt(three_syllable.size - 1)]} ${four_syllable[random.nextInt(four_syllable.size - 1)]},""" + System.lineSeparator()
                stanza =
                    stanza + two_syllable[random.nextInt(two_syllable.size - 1)] + " " + one_syllable[random.nextInt(one_syllable.size - 1)] + " " +  two_syllable[random.nextInt(two_syllable.size - 1)] + "."
                haiku!!.text = stanza
            }
        }
    }

    companion object {
        //private String reqUrl = "http://rhymebrain.com/talk?function=getRhymes&word=";    // call rhymebrain
        //private const val REQUEST_METHOD_GET = "GET"
        //private const val TAG_HTTP_URL_CONNECTION = "HTTP_URL_CONNECTION"
        private const val MAX_RESULTS = 50
    }
}


