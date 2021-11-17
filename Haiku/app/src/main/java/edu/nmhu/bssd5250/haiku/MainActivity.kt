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


class MainActivity : AppCompatActivity() {
    private var word1: EditText? = null
    private var word2: EditText? = null
    private var word3: EditText? = null
    private var haiku: TextView? = null
    private var submit: Button? = null
    private var output: String? = null
    //private val reqUrl = "http://rhymebrain.com/talk?" // call rhymebrain

    //private Bundle b = new Bundle();
    // establish syllable arrays
    private val one_syllable: ArrayList<String> = ArrayList()
    private val two_syllable: ArrayList<String> = ArrayList()
    private val three_syllable: ArrayList<String> = ArrayList()
    private val four_syllable: ArrayList<String> = ArrayList()
    private val five_syllable: ArrayList<String> = ArrayList()
    private val six_syllable: ArrayList<String> = ArrayList()
    private val seven_syllable: ArrayList<String> = ArrayList()

    // establish syllable arrays for word #1
    private val word1_one_syllable = ArrayList<String>()
    private val word1_two_syllable = ArrayList<String>()
    private val word1_three_syllable = ArrayList<String>()
    private val word1_four_syllable = ArrayList<String>()
    private val word1_five_syllable = ArrayList<String>()
    private val word1_six_syllable = ArrayList<String>()
    private val word1_seven_syllable = ArrayList<String>()

    // establish syllable arrays for word #2
    private val word2_one_syllable = ArrayList<String>()
    private val word2_two_syllable = ArrayList<String>()
    private val word2_three_syllable = ArrayList<String>()
    private val word2_four_syllable = ArrayList<String>()
    private val word2_five_syllable = ArrayList<String>()
    private val word2_six_syllable = ArrayList<String>()
    private val word2_seven_syllable = ArrayList<String>()

    // establish syllable arrays for word #3
    private val word3_one_syllable = ArrayList<String>()
    private val word3_two_syllable = ArrayList<String>()
    private val word3_three_syllable = ArrayList<String>()
    private val word3_four_syllable = ArrayList<String>()
    private val word3_five_syllable = ArrayList<String>()
    private val word3_six_syllable = ArrayList<String>()
    private val word3_seven_syllable = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect XML to code
        word1 = findViewById<View>(R.id.word1) as EditText
        word2 = findViewById<View>(R.id.word2) as EditText
        word3 = findViewById<View>(R.id.word3) as EditText
        haiku = findViewById<View>(R.id.haiku_text) as TextView
//        submit = findViewById<View>(R.id.submit) as Button
//        submit!!.setOnClickListener { processWords() }
        val genHaiku = findViewById<View>(R.id.haiku) as Button
        genHaiku.setOnClickListener {
            Log.d("Gen Haiku", "Generate Haiku")
            processWords()
        }
    }

    private fun processWords() {

        // get user input fro each of the words
        val word01: String = word1!!.text.toString()
        if (word1!!.text.toString().isEmpty()) word1!!.error = "Enter a word."
        startSendHttpRequestThread(word01, '1')

        val word02: String = word2!!.text.toString()
        if (word2!!.text.toString().isEmpty()) word2!!.error = "Enter a word."
        startSendHttpRequestThread(word02, '2')

        val word03: String = word3!!.text.toString()
        if (word3!!.text.toString().isEmpty()) word3!!.error = "Enter a word."
        startSendHttpRequestThread(word03, '3')

        // use the words to make a Haiku
        makeHaiku()

    }
    
    
    // ORIGINAl THREAD
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
                    "5" -> five_syllable.add(word)
                    "6" -> six_syllable.add(word)
                    "7" -> seven_syllable.add(word)
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
        var wordIdx1: Int = 0
        var wordIdx2: Int = 0
        var wordIdx3: Int = 0
        var wordIdx4: Int = 0
        var wordIdx5: Int = 0
        var wordIdx6: Int = 0
        var wordIdx7: Int = 0

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

        Log.i("one_syllable.size: ", one_syllable.size.toString())
        Log.i("two_syllable.size: ", two_syllable.size.toString())
        Log.i("three_syllable.size: ", three_syllable.size.toString())
        Log.i("four_syllable.size: ", four_syllable.size.toString())
        Log.i("five_syllable.size: ", five_syllable.size.toString())
        Log.i("six_syllable.size: ", six_syllable.size.toString())
        Log.i("seven_syllable.size: ", seven_syllable.size.toString())

        // Randomly choose Haiku pattern
        // line 1: 5 syllable
        // line 2: 7 syllable
        // line 3. 5 syllables
        var haiku_pattern: String
        when (random.nextInt(5 - 1 + 1)) {
            1 -> {
                haiku_pattern = """${two_syllable[random.nextInt(two_syllable.size - 1)]} ${three_syllable[random.nextInt(three_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${three_syllable[random.nextInt(three_syllable.size - 1)]} ${four_syllable[random.nextInt(four_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + three_syllable[random.nextInt(three_syllable.size - 1)] + " " + two_syllable[random.nextInt(two_syllable.size - 1)] + "."
                haiku!!.text = haiku_pattern
            }
            2 -> {
                haiku_pattern = """
                    ${five_syllable[random.nextInt(five_syllable.size - 1)]},
                    
                    """.trimIndent()
                haiku_pattern =
                    """$haiku_pattern${three_syllable[random.nextInt(three_syllable.size - 1)]} ${four_syllable[random.nextInt(four_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + four_syllable[random.nextInt(four_syllable.size - 1)] + " " + one_syllable[random.nextInt(one_syllable.size - 1)] + "."
                haiku!!.text = haiku_pattern
            }
            3 -> {
                haiku_pattern = """${three_syllable[random.nextInt(three_syllable.size - 1)]} ${two_syllable[random.nextInt(two_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${one_syllable[random.nextInt(one_syllable.size - 1)]} ${six_syllable[random.nextInt(six_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + two_syllable[random.nextInt(two_syllable.size - 1)] + " " + three_syllable[random.nextInt(three_syllable.size - 1)] + "."
                haiku!!.text = haiku_pattern
            }
            4 -> {
                haiku_pattern = """${four_syllable[random.nextInt(four_syllable.size - 1)]} ${one_syllable[random.nextInt(one_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${three_syllable[random.nextInt(three_syllable.size - 1)]} ${two_syllable[random.nextInt(two_syllable.size - 1)]} ${two_syllable[random.nextInt(two_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + two_syllable[random.nextInt(two_syllable.size - 1)] + " " + three_syllable[random.nextInt(three_syllable.size - 1)]+ "."
                haiku!!.text = haiku_pattern
            }
            5 -> {
                haiku_pattern = """${three_syllable[random.nextInt(three_syllable.size - 1)]} ${two_syllable[random.nextInt(two_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${four_syllable[random.nextInt(four_syllable.size - 1)]} ${three_syllable[random.nextInt(three_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + one_syllable[random.nextInt(one_syllable.size - 1)] + " " + three_syllable[random.nextInt(three_syllable.size - 1)] + " " + one_syllable[random.nextInt(one_syllable.size - 1)] + "."
                haiku!!.text = haiku_pattern
            }
            else -> {
                haiku_pattern = """${two_syllable[random.nextInt(two_syllable.size - 1)]} ${three_syllable[random.nextInt(three_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    """$haiku_pattern${three_syllable[random.nextInt(three_syllable.size - 1)]} ${four_syllable[random.nextInt(four_syllable.size - 1)]},""" + System.lineSeparator()
                haiku_pattern =
                    haiku_pattern + five_syllable[random.nextInt(five_syllable.size - 1)] + " " + two_syllable[random.nextInt(two_syllable.size - 1)] + "."
                haiku!!.text = haiku_pattern
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