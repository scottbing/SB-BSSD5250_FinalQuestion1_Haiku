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
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class ExtraActivity : AppCompatActivity() {
    private var word1: EditText? = null
    private var word2: EditText? = null
    private var word3: EditText? = null
    private var haiku: TextView? = null

    /*private String output;
    private final String reqUrl = "http://rhymebrain.com/talk?";    // call rhymebrain
    private static final String REQUEST_METHOD_GET = "GET";
    private static final String TAG_HTTP_URL_CONNECTION = "HTTP_URL_CONNECTION";*/
    //private static final int MAX_RESULTS = 100;
    //private Bundle b = new Bundle();
    // establish syllable arrays
    private val one_syllable: ArrayList<String>? = ArrayList()
    private val two_syllable: ArrayList<String>? = ArrayList()
    private val three_syllable: ArrayList<String>? = ArrayList()
    private val four_syllable: ArrayList<String>? = ArrayList()
    private val five_syllable: ArrayList<String>? = ArrayList()
    private val six_syllable: ArrayList<String>? = ArrayList()
    private val seven_syllable: ArrayList<String>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect XML to code
        word1 = findViewById<View>(R.id.word1) as EditText
        word2 = findViewById<View>(R.id.word2) as EditText
        word3 = findViewById<View>(R.id.word3) as EditText
        haiku = findViewById<View>(R.id.haiku_text) as TextView
        val submit = findViewById<View>(R.id.submit) as Button
        submit.setOnClickListener { arg0: View? -> processWords() }
        val genHaiku = findViewById<View>(R.id.haiku) as Button
        genHaiku.setOnClickListener { arg0: View? ->
            Log.d("Gen Haiku", "Generate Haiku")
            makeHaiku()
        }
    }

    private fun processWords() {
        val word01: String
        val word02: String
        val word03: String

        // get user input fro each of the words
        word01 = word1!!.text.toString()
        if (word1!!.text.toString().length == 0) word1!!.error = "Enter a word."
        startSendHttpRequestThread(word01)
        word02 = word2!!.text.toString()
        if (word2!!.text.toString().length == 0) word2!!.error = "Enter a word."
        startSendHttpRequestThread(word02)
        word03 = word3!!.text.toString()
        if (word3!!.text.toString().length == 0) word3!!.error = "Enter a word."
        startSendHttpRequestThread(word03)

        // use the words to make a Haiku
        makeHaiku()
    }

    /* Start a thread to send http request to web server use HttpURLConnection object. */
    private fun startSendHttpRequestThread(word: String) {
        val sendHttpRequestThread: Thread = object : Thread() {
            override fun run() {
                // using Panel 7.2.21 as a model- The Movie API

                //make empty URL and connection
                val url: URL
                var ur1Connection: HttpURLConnection? = null //HttpsURLConnection aiso avaitab1e
                try {

                    //String service = reqUrl;    // call rhymebrain
                    val service = "https://rhymebrain.com/talk?" // call rhymebrain
                    //String parm = "getRhymes&word=" + word;
                    //String queryString = URLEncoder.encode(parm, "UTF-8");
                    //String queryString = "getRhymes&word=" + word + "&maxResults=" + String.valueOf(MAX_RESULTS);
                    val queryString = "getRhymes&word=$word"
                    //try to process url and connect to it
                    url = URL(service + "function=" + queryString)
                    Log.d("which URL: ", url.toString())
                    ur1Connection = url.openConnection() as HttpURLConnection
                    ur1Connection.requestMethod = "GET"

                    // Set connection timeout and read timeout value.
                    ur1Connection!!.connectTimeout = 700000
                    ur1Connection.readTimeout = 700000
                    val inputStream = ur1Connection.inputStream
                    val br = BufferedReader(InputStreamReader(inputStream))
                    val b = StringBuilder()
                    var input: String?
                    while (br.readLine().also { input = it } != null) {
                        b.append(input)
                    }
                    Log.d("Network", b.toString())
                    parseJSON(b.toString())
                    val i = 0
                } catch (e: Exception) {
                    Log.d("Network", e.toString())
                } finally {
                    if (ur1Connection != null) {
                        ur1Connection.disconnect()
                        ur1Connection = null
                    }
                }
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
                    "1" -> one_syllable!!.add(word)
                    "2" -> two_syllable!!.add(word)
                    "3" -> three_syllable!!.add(word)
                    "4" -> four_syllable!!.add(word)
                    "5" -> five_syllable!!.add(word)
                    "6" -> six_syllable!!.add(word)
                    "7" -> seven_syllable!!.add(word)
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
        var wordIdx1 = 0
        var wordIdx2 = 0
        var wordIdx3 = 0
        var wordIdx4 = 0
        var wordIdx5 = 0
        var wordIdx6 = 0
        var wordIdx7 = 0

        // check if arrays are populated
        if (one_syllable != null) {
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
            wordIdx1 = random.nextInt(one_syllable.size - 1)
        }
        if (two_syllable != null) {
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
            wordIdx2 = random.nextInt(two_syllable.size - 1)
        }
        if (three_syllable != null) {
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
            wordIdx3 = random.nextInt(three_syllable.size - 1)
        }
        if (four_syllable != null) {
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
            wordIdx4 = random.nextInt(four_syllable.size - 1)
        }
        if (five_syllable != null) {
            if (five_syllable.size > 0) {
                hasWords[4] = true
            } else {
                // just in case the service is unavailable
                five_syllable.add("impresario")
                five_syllable.add("archipelago")
                five_syllable.add("pianissimo")
                five_syllable.add("generality")
                five_syllable.add("circularity")
            }
            wordIdx5 = random.nextInt(five_syllable.size - 1)
        }
        if (six_syllable != null) {
            if (six_syllable.size > 0) {
                hasWords[5] = true
            } else {
                // just in case the service is unavailable
                six_syllable.add("colonialism")
                six_syllable.add("materialism")
                six_syllable.add("emotionalism")
                six_syllable.add("congeniality")
                six_syllable.add("irrationality")
            }
            wordIdx6 = random.nextInt(six_syllable.size - 1)
        }
        if (seven_syllable != null) {
            if (seven_syllable.size > 0) {
                hasWords[6] = true
            } else {
                // just in case the service is unavailable
                seven_syllable.add("colonialism")
                seven_syllable.add("Arteriosclerosis")
                seven_syllable.add("Artificiality")
                seven_syllable.add("Autobiographical")
                seven_syllable.add("Editorializing")
            }
            wordIdx7 = random.nextInt(seven_syllable.size - 1)
        }

        // Randomly choose Haiku pattern
        // line 1: 5 syllable
        // line 2: 7 syllable
        // line 3. 5 syllables
        var haiku_pattern: String
        val patternIdx = random.nextInt(5 - 1 + 1)
        when (patternIdx) {
            1 -> {
                haiku_pattern = """${two_syllable!![wordIdx2]} ${three_syllable!![wordIdx3]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${three_syllable[wordIdx3]} ${four_syllable!![wordIdx4]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + three_syllable[wordIdx3] + " " + two_syllable[wordIdx2] + "."
                haiku!!.text = haiku_pattern
            }
            2 -> {
                haiku_pattern = """
                    ${five_syllable!![wordIdx5]},
                    
                    """.trimIndent()
                haiku_pattern =
                    """$haiku_pattern${three_syllable!![wordIdx3]} ${four_syllable!![wordIdx4]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + four_syllable[wordIdx4] + " " + one_syllable!![wordIdx1] + "."
                haiku!!.text = haiku_pattern
            }
            3 -> {
                haiku_pattern = """${three_syllable!![wordIdx3]} ${two_syllable!![wordIdx2]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${one_syllable!![wordIdx1]} ${six_syllable!![wordIdx6]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + two_syllable[wordIdx2] + " " + three_syllable[wordIdx3] + "."
                haiku!!.text = haiku_pattern
            }
            4 -> {
                haiku_pattern = """${four_syllable!![wordIdx4]} ${one_syllable!![wordIdx1]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${three_syllable!![wordIdx3]} ${two_syllable!![wordIdx2]} ${two_syllable[wordIdx2]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + two_syllable[wordIdx2] + " " + three_syllable[wordIdx3] + "."
                haiku!!.text = haiku_pattern
            }
            5 -> {
                haiku_pattern = """${three_syllable!![wordIdx3]} ${two_syllable!![wordIdx2]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${four_syllable!![wordIdx4]} ${three_syllable[wordIdx3]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + one_syllable!![wordIdx1] + " " + three_syllable[wordIdx3] + " " + one_syllable[wordIdx1] + "."
                haiku!!.text = haiku_pattern
            }
            else -> {
                haiku_pattern = """${two_syllable!![wordIdx2]} ${three_syllable!![wordIdx3]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${three_syllable[wordIdx3]} ${four_syllable!![wordIdx4]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + five_syllable!![wordIdx5] + " " + two_syllable[wordIdx2] + "."
                haiku!!.text = haiku_pattern
            }
        }
    }
}