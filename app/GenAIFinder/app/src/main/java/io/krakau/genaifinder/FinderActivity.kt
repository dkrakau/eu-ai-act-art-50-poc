package io.krakau.genaifinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.json.JSONArray
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class FinderActivity : AppCompatActivity() {

    // Toolbar
    private lateinit var appBarConfiguration: AppBarConfiguration

    // View variables
    private lateinit var finderLinearLayout: LinearLayout
    private lateinit var itemImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var simularityTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var originTagCardView: CardView
    private lateinit var originTagTextView: TextView
    private lateinit var resultLinearLayout: LinearLayout
    private lateinit var loadingConstraintLayout: ConstraintLayout
    private lateinit var loadingImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)*/
        setContentView(R.layout.activity_finder)
        setSupportActionBar(findViewById(R.id.toolbar))

        // bindings
        finderLinearLayout = findViewById<LinearLayout>(R.id.finderLinearLayout)
        itemImageView = findViewById<ImageView>(R.id.itemImageView)
        titleTextView = findViewById<TextView>(R.id.titleTextView)
        descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        simularityTextView = findViewById<TextView>(R.id.simularityTextView)
        dateTextView = findViewById<TextView>(R.id.dateTextView)
        timeTextView = findViewById<TextView>(R.id.timeTextView)
        originTagCardView = findViewById<CardView>(R.id.originTagCardView)
        originTagTextView = findViewById<TextView>(R.id.originTagTextView)
        resultLinearLayout = findViewById<LinearLayout>(R.id.resultLinearLayout)
        loadingConstraintLayout = findViewById<ConstraintLayout>(R.id.loadingConstraintLayout)
        loadingImageView = findViewById<ImageView>(R.id.loadingImageView)

        // Send request
        var genAiFinderData = JSONArray(isccData())
        // Disable loadingView on received data and enable finderView
        // render genaifinderdata
        renderData(genAiFinderData)



    }

    fun renderData(genAiFinderData: JSONArray) {
        for (i in 0 until genAiFinderData.length()) {
            /*if(i == 0) {
                // create search item
            }
            else {*/

            var thumbnail = genAiFinderData.getJSONObject(i).getString("thumbnail")
            var title = genAiFinderData.getJSONObject(i).getString("filename")
            var description = genAiFinderData.getJSONObject(i).getString("name")

            val currentTime = System.currentTimeMillis()

            var listItem = createListItem(
                    thumbnail,
                    title,
                    description,
                    "100% Simularity",
                    "Midjourney",
                    getDate(currentTime),
                    getTime(currentTime),
                )
            listItem.setOnClickListener {
                Log.d("BUTTONS", "User tapped item in list")
                Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
            }

            if(i == genAiFinderData.length() - 1) {
                listItem.background = null
            }

            resultLinearLayout.addView(listItem)
            //}
        }
    }

    fun createSearchItem(imageUrl: String, title: String, description: String) {
        /*Picasso.get().load(imageData)
                    .placeholder(R.drawable.placeholder_image)
                    .into(itemImageView)*/
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.placeholderOf(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error))
            .into(itemImageView)
        titleTextView.text = title
        descriptionTextView.text = description

        val currentTime = System.currentTimeMillis()
        dateTextView.hint = getDate(currentTime)
        timeTextView.hint = getTime(currentTime)

        originTagTextView.text = imageUrl.toUri().host
    }

    fun createListItem(imageData: String, title: String, description: String, simularity: String, origin: String, date: String, time: String): LinearLayout {
        // Load LinearLayout for items
        val inflater = LayoutInflater.from(this) // or getLayoutInflater()
        var listItemLinearLayout = inflater.inflate(R.layout.content_list_item, resultLinearLayout, false) as LinearLayout
        // Find elements within LinearLayout by id
        var itemImageView = listItemLinearLayout.findViewById<ImageView>(R.id.itemImageView)
        var titleTextView = listItemLinearLayout.findViewById<TextView>(R.id.titleTextView)
        var descriptionTextView = listItemLinearLayout.findViewById<TextView>(R.id.descriptionTextView)
        var simularityTextView = listItemLinearLayout.findViewById<TextView>(R.id.simularityTextView)
        var dateTextView = listItemLinearLayout.findViewById<TextView>(R.id.dateTextView)
        var timeTextView = listItemLinearLayout.findViewById<TextView>(R.id.timeTextView)
        var originTagCardView = listItemLinearLayout.findViewById<CardView>(R.id.originTagCardView)
        var originTagTextView = listItemLinearLayout.findViewById<TextView>(R.id.originTagTextView)

        // Set values
        /*Picasso.get().load(imageData)
                    .placeholder(R.drawable.placeholder_image)
                    .into(itemImageView)*/
        Glide.with(this)
            .load(imageData)
            .apply(RequestOptions.placeholderOf(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error))
            .into(itemImageView)
        titleTextView.text = title
        descriptionTextView.text = description
        simularityTextView.text = simularity

        dateTextView.hint = date
        timeTextView.hint = time

        originTagTextView.text = origin

        return listItemLinearLayout
    }

    fun getDate(currentTime: Long): String {
        val instant = Instant.ofEpochMilli(currentTime)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return dateTime.format(formatter)
    }

    fun getTime(currentTime: Long): String {
        val instant = Instant.ofEpochMilli(currentTime)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return dateTime.format(formatter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this@FinderActivity, SettingsActivity::class.java).apply {
                    putExtra("callingActivity", FinderActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            R.id.action_information -> {
                startActivity(Intent(this@FinderActivity, InformationActivity::class.java).apply {
                    putExtra("callingActivity", FinderActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun isccData(): String {
        val largeIsccData = """
            [
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC6BF6GSH67774G2DH7DTWLFEFCNMT4I4FLLYFPS5X2GFIYNZHLQOA",
              "name": "arena",
              "media_id": "06905h74qu548",
              "content": "http://localhost:8000/api/v1/media/06905h74qu548",
              "mode": "image",
              "filename": "arena.jpg",
              "filesize": 1252407,
              "mediatype": "image/jpeg",
              "width": 3264,
              "height": 2448,
              "thumbnail": "data:image/webp;base64,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",
              "metahash": "1e202951603325fe5fe662de01018c1364402cc0ae7d03d0f5ab7cad2ca8e4ddbd62",
              "datahash": "1e206fa315186e4eb8380a407e1a58bc737e9165f7c8470bb4e44eeb4d65d23d7ece"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECRK4SBOP3GPG6RT6TH5RWATILFCKMN2M6PI7KT6Z6JOSDSZ7WRY6Q",
              "name": "berries",
              "media_id": "06905gmt69tc4",
              "content": "http://localhost:8000/api/v1/media/06905gmt69tc4",
              "mode": "image",
              "filename": "berries.jpg",
              "filesize": 1365293,
              "mediatype": "image/jpeg",
              "width": 4240,
              "height": 2832,
              "thumbnail": "data:image/webp;base64,UklGRtYPAABXRUJQVlA4IMoPAACQMgCdASqAAFUAPpk0k0mloqGhOUwAsBMJbAC++cDU3oRmf41fisBF5W/T/rPVZ+jvYA55fmE87H0q/7P0h+pa6J31Wf7x/1uoA///t75qPeP44f8+wDUI7w8V+9P4yahfs3zsXkTgjv54HmqPkB8EJQE8mn/P8fX1n7Bf7Ddcgs3EHd8Y/j4+RwW7i/JESJkuB+d7nBZp8ERiHvEERa2mz870dM0VIwyKok2ntVKe7ENftxleD0uGeh7Suqvm4ORIPYpwl5B/NUWtukf3//cIR+rJyiVAL81J6sKc/zBsWQlNnXyrF5Nm1agsjrvQXAKFdycThuy9J1n3n5qErvzwBQj6dKqHy9h8YtNTJwBaJjJZ6L7xY0JLVxsN6He6jAv1+MkJgSvhOI9t6WkuFoDcp5AuK0HVJ37rppOpeZg+81h2oI3iEcu5IF08Rm+PUH0B/F5vPKIzcktvlj5awg7qoReFCcxG+ahlB/uNpYJ7DyOQIHZOjOEnCxIIKO2NQIjDYW7GFhqAXgymnHgW81/7+daFx1OtjcvGa0QxgAD+4nIJVhmq+32ftRk9jaj+IhB0nzz2LoNn+JaGiRpjoLLkrQ6VMK41jpIfoRrhCcnd6Kr2O8JZQyWibAlDV5cXi08jJy+YPFoqD+EC0wtDSkQbSpt49fkj0dp3IHwwH/scB+atA53eEHEJ3z2myTH97ksL7yxcSBrCJyJK+keYM9agebHBG4VmdJ/KjH2oMFnfpJjq1W4abgSlV/4UoCuZ9fFBpvIRHxEJuUjX0LtQyqg0lpvylLHbXj9PU6sw3VymwdE5sRwRqMseSRSUY4UesEgC5p/3BJoWE4ymsOD9LhXsyKLdaNnljPub7H8y+ihVj+GAvLHwrXrfYKKKA2X3dJ8O+FO5B22QrHz/v7rVIzAwB4moLf3YHAAEixbed71WoxeekE+2zTohnuaNmwhbztGIi/mmhj6W8OiinV60pEUtmyODV1hc651Ni8gF68aSNENsyisPaf5ggpTJnqXU3rlAa9aGmFZrX3ov8amIT9OOOhAGIZEYwbC9zFdo8vq43vE/r88shkgl6LJm1g0cu8nIi2WBvk/cqMqlsFSNzoK7sUJZOKr9vP8/GErmIdMe2w+TB6SW8A0YWpqtZS+TTqEdW89OfjnnZyQ7i66F0OEcMiffP1zehQX1VvFr+arBdJgIEFIQuvqw7ukLXDtvfTy8tFTrDFHvPqXODqGoSJFZCyQApudY/ex3qO8oXVaj2gcnmESUFVeP/pEV8W6gXYixyigw5Dpcr1S6bb5UwA7FL5Tza8Lxqz2JluNUqJTRQQqfCxy1LJNNy7b3vxumkHcxCvt97nLI5qf/AVn3D3bcJiLnmyLACs25uiZqzlYsfndnOvQMcdJs0oey5UawlbXB7+rMUWN11BW0RMiAzrJvrUoWY7j6A/ok2Qbv8mxjnWHQPda3exY1+dM04tm9R4acjLftj/dk5OXCohmUbw+k/YqB4sJU3b8VId4fcsE4ui9N7ZvgfppT0PYEV/fPODlE2JTtwB4AOfrGHQB4/S/uUIcwKoYI/U7Z9lVB/Vr7fQnGK/2Leyb4ys2mreAOgWMERhALc4kdX6DGIZv4zg5NHTHro7/WxlDWr2VxX2xQMXLtCvOMgqXFUfiqUGa6Oi+J8qEeG1trlg48rbfncHtKJyiy6vswR6lt+6i4tJ9VkxP1P2J0TV6bPQmrdSAkF2a/ct+b9lQNXMiKC0BsE0oLJVwxHyKvfYLhkn1GQUjHnojtevI8K3Wo78Go6G+Z5bxhDTAwr1HpKcVjIY0OuRlzSwvyH7y4Mo/k0s9BBlYVSXhfvI+VNHmFH4eeUmN9ffAyh0YqWt41YpDnDvQL0mQ7ysfxfqHTQhVXqYUs8+sE6tkfLXnTcPcEyrMwSKIKO5WaMFQ4xlH6RYBmU4ADd/PZKhxYJz0os81VysrTb+L88DQFqryPF8ywn+E3DSC+uevts0g+QGky8YgTZprgsRHiGqpnBY/SJxmzHbPnDQ/BZPdOTegHxsQ+k+iVixyqQ+zlM3zTc+Av5UZRn19q2FwoNvY+de9xM/xEqkZ/+h9DHbBPCE+Dc3xKMX930ahTkIX4N/3XohDlV2PiLv5L0+l8HpkLKZpfTbQCI2+QXaMPZx+6mV0L+Qu+5sYpwdoj3mkqq85NE4/x5eVGWGQZx6xW8W5KfKrB2KAHglWL94D6zSgR/LOgiuCvlXXHOaaDu67XlmziIlxraoZvIb+uY/JJWIz6yLQbGPmkrRg2k08jKtMqSOZVxTptqvODTkg3/Dmd5Dvr7MNZaakbYQDiWcsWYpVXX070fMEZz+KKexXUAx/iNGS+YmNiuMN77ZEGXjJ3WSBuAhGF9oc5wjLspPLIBStZFfE9a6MpywY6vnECC3oe4fvZ3H+5FnhBerR0POM+yKca4QlsJO9+CNy3uZW6uVAp1ZQj37X4SfgtbmYZN4HPyDQb7xUkvzxHxyPJut5WTY+/8DABHXfI8JAzMcC8rD4uFIJGwK77qIRQER9XnANJrZuuMHEq0sf1plim/7wuS0BOou7Ny3T7Ax4ujhouGqqdUtf/eN1Ds5gCv+EJsIyplHkOusqDt6Ba1FtCxVSW93Kn1rqCEE8F3lPJcaE6k220i15zezuKVzZYLJxzMEaPp07kYvZBsVU0bRaFUld2LAA3pBziVUboC9KoWXEBaj5o85A0x9p6JkNeT0ZRxmT/dle74Av3m7RGX+qWavh3wMbgtlY2MQS7gQUM6paBD+2pUZ7vNEyHiGTrGBhIWMhqq2q61oDUabCpuHzOX354YjLkxOexP7S+aFLP00ThlzkL7dFVj4nmU++bPoiXt8uD3bejhgyHun9wnD+Mf4zfiNfY6N0Nhg2oo914xWO5rQ7njSGHUogDyM1zotMlq2rCIHfgZ+n2ctjf7ukt+dbUaMKTTOeeXXgpl7ifOvAuhq103sOp4oo77kdO3f+OITekOLtdriz2qvKj4RJy4JTIQaPLcqB0VP9kwgqSWBCEdU4JW5Rpgt7CjevZcLObmZtmvlKjqfl5H2TAUBZD72YM6pDzOMFOr+a1LLL8Z9YcxqJJzm4ZZ8boQb55/g0q6DIz1kI30rSNU0IVH/kcC/PN+/jhvq0n2YSTYfIzoe9/fuqw9TB7SFr31KMP7NI0mEfW51WvRwAriqQmpFjuyIHXiDOiv9hX/oMy3gNMFNkekzNA9iBUcnE/qUZCch4SF/meV8WQcEinLItScCHO/F7IsKS4jh46UGM0idseDbLFSBz7OZQYE5bc4NdS9bxOAHij2VOVIC+lveRW2ocMtSkm5DCBmcSLvHkEFPCqmZyK2jCo3+u136WYS4hVk3s0VTwbcnHLz6rDulz+0ZnJAWizcmyn1PDgRPr4XJ4DeU/3FR42b/FqFisYSxc5jRUMFiQid6dmvmfV2hkrCovAlF/YgF7Wu3j2SnMnzfCMJrTr5hJYXs89GfJbPK/E04AfwTZGazDFrn6AL3zAukdjsBf/owVCjpiOXV5ZjMfiqe5/5ocPOeLGuMP94wSGrFPh8qjoEopdrxan4uj2RXVbKWDX+EA+pDl42ZVOEgcD0cKXjiH1NjHOMQj76HlVi0dZ7sUnJf/NFx/W7Zvim2ZWTsDoSEuoTdlcLkvg6gkN90eXZxAofR8f2pXgqJ7+VJiWuYr5ITz7LtL654AUSRp6T+WA7eNMV8bV1QRn6lCI8x3z0Hk17VufOVVFxQMpLDIt2jvAPvMz1y/sidnt8/IDi/TwScdtsJnvbZg5Uy9mTtVSiJmD8vmeNrcetbDeretErhFZaarX9GGRYEhbDp7qgRT+MQfrK6hRi05L/TubKjmDTgrsGvrK8WNfArjblm1wmPUMtudLQo/58nLK1/mfZO853DDe0VmpdlnWXSJ8RgwD+zI9HalaghJpTX3W/DzSBA0RhcSVZAHZIHjbDLIroabMfIyO9FMMXsSgqMjPfVmciJOTvsQYdqjlOgbmFwLe+d7WOdoJ2EDrL8MUN05BZS6Xx56X4YSzHng+UtOMHFzxo7d01N/h7alRSj4wAwpkt8l0BKuvOUmzsb9gIaB9MYcF9MRckZAmkUGy7l4bdWJiTBxA2kAtBmPjw92TrjdLMfy4SLf4AAS7J8KMvsgqkNiFlpObmA7HeSclOW5JXhxTo4N+qHv50kbidKVCXVfNDiOTv0IfPUpqQ9MIA7ofJxdDTmC7KvKWii0mRbVwb8w/ALXYCNlVP04TZ1vtCu22Szf87T0qGeFfoUiOtZBkHxqc65QowvfYVHQacJht0VKvsMQ59vTxN+8Sm6mDDvjP4SlXlrnjTimAUo7BHz13WiueE/Ul6oX0Y0mqbHLosaLjeUiCiyz/QmD3ChnvJkzHxIBspK+F7d3RI7b9lawRjZdQW9987A6GWIZHestDNujpuwfUYjUcwScCg7knjYm+Yv4aKg0v0TPzsnj1QIssyyAv57rIG9TXDgD5aj8lmhv8aBbODh8N9EpD0uSbXQg9T8vKlJMGbap1lJyDqxa4tPloX4rMbatPeGGgocJH4U04zFtCnpBEvRehBBnTzvBQK9cCBzqFWiyyR83M9lnnrhkhHzuxFxEAQ1zeCzFRioy35ZufNzk2gt+zWDjHndGlRTXTis/sExr7W/5P3EfFFwU1+DXnauV4nv7DHbFqQGxoyTxxzDdlEPmZ1Wl3Bo8UIeMqxzNUK6zczQmBrnuEh/2faKXVGhHUMRNQ70F328kT1jha0iKKCAAjPhpiJ1u6Fp2Z/aV6qTPlxqMGAVgJVIVwnVTvh1gZgVakFbKZH54MdMeuCs0hy+N1535zC2mgYwbD4iaP2qpqwUgBLNq18x4gEesvfcUtRCWIIfWEvij4QLAUgvhGHeqKWFA0wNG0FE5t0FYMwCq19J3gkdz92njcwaRIWd0AGTAEWEArdqZ4Wm4Dl/VkkmwCXC95F14y65DdP1aI/gqX/z8thX+tUxpipuD+fJm52fG9w3dZIaUphUi/+IDD2Wokvp61YM2D7ejaggxfn6WgCOAv/m/6lHKk2VUe+HyUwB6/PxvCI9K0k4FJmU15z4WqUPfnKoD7egI9Ayt3wliUuVgLz8tKIV9KomCD/bRxslCb5I+3F9lmHTIzd1+OH8reFz6Da1eTUeU9xJzZZ/57lC6BAZ3qI34FHDqBGPyFp4vA+58QeYaOrfo4jdI32e/Y3bnTFp5W5C268m24ttHthjndV6Ka5z8aR/1sG0xVzQuQl5xS22pz7cJjf6unjPgAz/eCSc9p0jbL2MG4OV8oZ6GbWZVtcKEVOp1YL8WM8cni+6XPD275NvUWsObxFg41cZrQCS35Ik2gCbgldVnH9/G3zEZZ8OD5aj2jsHhagAAA",
              "metahash": "1e20b554d04b0ae2eeeee88521c8ac37595a5fb1ed5af9ed4434036d99f4bfa53c31",
              "datahash": "1e207c974872cfed1c7a5fdd464ff880ea3a4cba35d861c4b048e2a367f0fc733889"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC4L75HQI3I5PWF3ZYEHD7QH22GBAJHEUKXS2AJHMKBWALDBZKQNMA",
              "name": "book and phone",
              "media_id": "06905hd8nv0vo",
              "content": "http://localhost:8000/api/v1/media/06905hd8nv0vo",
              "mode": "image",
              "filename": "book-and-phone.jpg",
              "filesize": 491560,
              "mediatype": "image/jpeg",
              "width": 5304,
              "height": 3531,
              "thumbnail": "data:image/webp;base64,UklGRpADAABXRUJQVlA4IIQDAAAwFQCdASqAAFUAPrVQpEqnJSQprVc6STAWiWcA06B+bcSqMib3o3zTVWgz7yzfVJp9s1vBCwZS3UFS3YaThflVwFFwJs8pGD2AQlK12Va4uFGJTLQvAIlDxoaJBh1jB9I5LMlT0seDNvS64XSI49PbK6nlxRoiXzh/q2yXrP5C6y9BgaJiGid5LHvhCJPb6YC2w+evwYegNYunKA1VABg/KlbOFWxoIMyvHqNm0SIsQeDAAP7zls8eYEt2VTY9dwGh1D26riBX+V2CEBHyjb812o0Mnrf0/ek9NdvfFnwN42JOXl9bK4cfTPd4AqRPxCNURgivArqa3Msrl204XqZEXYxLPVhdpT9OFiy0pN/FocWNpZxbr2YtJIER14Pide9i4pJir4I7NUJIElUq1ERQ2jzv84uDqwxY/Agh/dyI6Mw2LFv7HSDA2/uYoetvK+21JUSEX1tWpvnfJs0PGr07qe9JiwAkCKGX0J1T5I9e3626nj7SJPdquTLxX5P2DsBqIsWD5OLOB+41tYGXuStPfJqonDZJKgHR4ezU5fOcB7eU+slQn9atiiIb+thr+0jiR6ARuWBabJElQB5Au0RJCStBkgDVYjkuxY12KAaHwu4wMalr1D0Wyw7b+xf4b6rsALXvhtGtMtY9anpzgD5790zmZXDGgFvVu1ouejPJEoA/9QU0ciWhDScJbpipjB5YVFqvuHllqNWUT5iNvgXf35y1s8IQQBxemosH1Co1GvjvHcDzxGP4ARFWQ4LUJcVof5vGHVcgXjxMnFVFVrz8QOBDfOf4QQlNwy+n2itcV02NFbdPvuumb+vCwhHqOzVqLP9hmGsqXGIZAkJihQilwI7quwnVa0L19LidS6EtIrVssx4XaPMkn0efkBRxYBZovfrt/mh9x6/mH0tffcYuYabJW7y817yDKRWAqXqIsBAgll3NQ5nJCAKuhmLZDzii4v1PTVxL9FISOp8PA8jRNj6S9e7WR0iCNU7Cc72faDNZkCMmu0aTFbersXNgZp9zGKfClS0oRgLyQ6izJhILgM579bCjdyvqnb/YdlHR0Ih1wAdjLZGWHxt2IWb2/v1gUb+pq2p7Hok8vHHFDglSYfWz9HXbEbu2X2cC/tMcli+rGXHPbSARRfEcoc0mAh54RzN+MT6NUyDO1YFxMDIAyVlYppoU615szgk6QBJSnDwJSAA=",
              "metahash": "1e20829e05c1cfae0d1b72756ef12d769530109460e36fa02ce2e108b48bd70c01bf",
              "datahash": "1e20141b01630e5506b089179251fd8d8b150fca173ff0d358d5f7712007d4d4f240"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECSRU7TX527VXG5URIUWJWYOGL47FKMTG6WX77GOL7TUWLV5F254GA",
              "name": "book with pencil",
              "media_id": "06905l3vur3te",
              "content": "http://localhost:8000/api/v1/media/06905l3vur3te",
              "mode": "image",
              "filename": "book-with-pencil.jpg",
              "filesize": 436493,
              "mediatype": "image/jpeg",
              "width": 5184,
              "height": 3416,
              "thumbnail": "data:image/webp;base64,UklGRgoDAABXRUJQVlA4IP4CAABQEwCdASqAAFQAPrVQoEwnJLSqrBVaqpAWiWUG+Bwx7j+A2/88Qb8VCp+r3zS+ztIAYtmClL4R93k7t7NgvQuJub/4CmWjIWUzo4v2mAXwLL/mpatzS5Qe92M5apcFSB2fYMCjGQRWhOwqRz7WrkCkTXIRT1Z1uWyfdbtdfce7o58b86sSarAZ4L3LCOzmwGJNiDli7Ao9QHyhX824SMt0p9cwAP7tQK94cR3QLJuYI8UlQRAFXmkDtoP/JFQhgeuIs4PuxDH3gOrdrr+qyb+ODLGTEjha5Sf5TeRlOyJ4afd7VOompObwoYeIFd8rYkfMntjlrxHhW0zjoUd6kqCC6NQQS4HKL+Tu7CuS9ew5KgPu58Y++Ghvgn5uJPRUYKAouM9AhAF3sWa6+jgsUCyl7wHivTJs2D8HM/o+A/I9+F+cRw+nmhMX0zch9J2CHk1MZVOl1FPDCdr3MDt2u6ub0EFpZrwR3im7nZ1ZcBaGds3n/UKPUWDpOdTiG1yX9vnfpUSpJLXe1ai1AgZHL9ET8ZwLA5WveZXgYFp4VUm8mC7QsM3t5P8uT8oAuFKVGimwenGtq0r2q462oYPOx2Ag7ruT0O4cQvAptP5buhnBhoHStpgwUJbihK7oueVKzCKaLUz1qk0wv1KzSJmFcyYtQjQz1RP6zM+whz7F7SC+VrjFSu+qXNvvcC8l6/q3njH38FoIZNIDBLVt01Tg2dlnC+g5fWWUsPr4RlztMJpIbaiX9gfFjkE6TZbhxZQKGQs44j6q3/HV2UM+SnXj3slbKf5OKHwkgfzedGrNvQGxeYpKllVnjuhANOGZc+qtYwShv1Vaehz2KMR2Vd6Yb3u5WA20d2rEAWLyrheBoTv8LB23vOxBoaLuV/YgooLMYQQ/HF0mFdbcE+ddJA1y/QIy+X6X0CEfLTICcs9gXCbQocIA9wqesmPaVwq6caf7B0u2Wh1ODatKTV2KMPVyEayl0wz8hTx3t82bMO4lnqgJo6wKQTii/YhcbeVF0AAA",
              "metahash": "1e200dcc58df1635374db14a1b9fb5b8600d0c7ebeb1790e970ba64091dfac2883d2",
              "datahash": "1e20ff3a5975e975de18b0b0438c82bbe91d599cdc4000dae6720ed0bac977669e35"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC6BF6GSH67774G2DH7DTWLFEFCNMT4I4FLLYFPS5X2GFIYNZHLQOA",
              "name": "arena",
              "media_id": "06905h74qu548",
              "content": "http://localhost:8000/api/v1/media/06905h74qu548",
              "mode": "image",
              "filename": "arena.jpg",
              "filesize": 1252407,
              "mediatype": "image/jpeg",
              "width": 3264,
              "height": 2448,
              "thumbnail": "data:image/webp;base64,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",
              "metahash": "1e202951603325fe5fe662de01018c1364402cc0ae7d03d0f5ab7cad2ca8e4ddbd62",
              "datahash": "1e206fa315186e4eb8380a407e1a58bc737e9165f7c8470bb4e44eeb4d65d23d7ece"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECRK4SBOP3GPG6RT6TH5RWATILFCKMN2M6PI7KT6Z6JOSDSZ7WRY6Q",
              "name": "berries",
              "media_id": "06905gmt69tc4",
              "content": "http://localhost:8000/api/v1/media/06905gmt69tc4",
              "mode": "image",
              "filename": "berries.jpg",
              "filesize": 1365293,
              "mediatype": "image/jpeg",
              "width": 4240,
              "height": 2832,
              "thumbnail": "data:image/webp;base64,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",
              "metahash": "1e20b554d04b0ae2eeeee88521c8ac37595a5fb1ed5af9ed4434036d99f4bfa53c31",
              "datahash": "1e207c974872cfed1c7a5fdd464ff880ea3a4cba35d861c4b048e2a367f0fc733889"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC4L75HQI3I5PWF3ZYEHD7QH22GBAJHEUKXS2AJHMKBWALDBZKQNMA",
              "name": "book and phone",
              "media_id": "06905hd8nv0vo",
              "content": "http://localhost:8000/api/v1/media/06905hd8nv0vo",
              "mode": "image",
              "filename": "book-and-phone.jpg",
              "filesize": 491560,
              "mediatype": "image/jpeg",
              "width": 5304,
              "height": 3531,
              "thumbnail": "data:image/webp;base64,UklGRpADAABXRUJQVlA4IIQDAAAwFQCdASqAAFUAPrVQpEqnJSQprVc6STAWiWcA06B+bcSqMib3o3zTVWgz7yzfVJp9s1vBCwZS3UFS3YaThflVwFFwJs8pGD2AQlK12Va4uFGJTLQvAIlDxoaJBh1jB9I5LMlT0seDNvS64XSI49PbK6nlxRoiXzh/q2yXrP5C6y9BgaJiGid5LHvhCJPb6YC2w+evwYegNYunKA1VABg/KlbOFWxoIMyvHqNm0SIsQeDAAP7zls8eYEt2VTY9dwGh1D26riBX+V2CEBHyjb812o0Mnrf0/ek9NdvfFnwN42JOXl9bK4cfTPd4AqRPxCNURgivArqa3Msrl204XqZEXYxLPVhdpT9OFiy0pN/FocWNpZxbr2YtJIER14Pide9i4pJir4I7NUJIElUq1ERQ2jzv84uDqwxY/Agh/dyI6Mw2LFv7HSDA2/uYoetvK+21JUSEX1tWpvnfJs0PGr07qe9JiwAkCKGX0J1T5I9e3626nj7SJPdquTLxX5P2DsBqIsWD5OLOB+41tYGXuStPfJqonDZJKgHR4ezU5fOcB7eU+slQn9atiiIb+thr+0jiR6ARuWBabJElQB5Au0RJCStBkgDVYjkuxY12KAaHwu4wMalr1D0Wyw7b+xf4b6rsALXvhtGtMtY9anpzgD5790zmZXDGgFvVu1ouejPJEoA/9QU0ciWhDScJbpipjB5YVFqvuHllqNWUT5iNvgXf35y1s8IQQBxemosH1Co1GvjvHcDzxGP4ARFWQ4LUJcVof5vGHVcgXjxMnFVFVrz8QOBDfOf4QQlNwy+n2itcV02NFbdPvuumb+vCwhHqOzVqLP9hmGsqXGIZAkJihQilwI7quwnVa0L19LidS6EtIrVssx4XaPMkn0efkBRxYBZovfrt/mh9x6/mH0tffcYuYabJW7y817yDKRWAqXqIsBAgll3NQ5nJCAKuhmLZDzii4v1PTVxL9FISOp8PA8jRNj6S9e7WR0iCNU7Cc72faDNZkCMmu0aTFbersXNgZp9zGKfClS0oRgLyQ6izJhILgM579bCjdyvqnb/YdlHR0Ih1wAdjLZGWHxt2IWb2/v1gUb+pq2p7Hok8vHHFDglSYfWz9HXbEbu2X2cC/tMcli+rGXHPbSARRfEcoc0mAh54RzN+MT6NUyDO1YFxMDIAyVlYppoU615szgk6QBJSnDwJSAA=",
              "metahash": "1e20829e05c1cfae0d1b72756ef12d769530109460e36fa02ce2e108b48bd70c01bf",
              "datahash": "1e20141b01630e5506b089179251fd8d8b150fca173ff0d358d5f7712007d4d4f240"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECSRU7TX527VXG5URIUWJWYOGL47FKMTG6WX77GOL7TUWLV5F254GA",
              "name": "book with pencil",
              "media_id": "06905l3vur3te",
              "content": "http://localhost:8000/api/v1/media/06905l3vur3te",
              "mode": "image",
              "filename": "book-with-pencil.jpg",
              "filesize": 436493,
              "mediatype": "image/jpeg",
              "width": 5184,
              "height": 3416,
              "thumbnail": "data:image/webp;base64,UklGRgoDAABXRUJQVlA4IP4CAABQEwCdASqAAFQAPrVQoEwnJLSqrBVaqpAWiWUG+Bwx7j+A2/88Qb8VCp+r3zS+ztIAYtmClL4R93k7t7NgvQuJub/4CmWjIWUzo4v2mAXwLL/mpatzS5Qe92M5apcFSB2fYMCjGQRWhOwqRz7WrkCkTXIRT1Z1uWyfdbtdfce7o58b86sSarAZ4L3LCOzmwGJNiDli7Ao9QHyhX824SMt0p9cwAP7tQK94cR3QLJuYI8UlQRAFXmkDtoP/JFQhgeuIs4PuxDH3gOrdrr+qyb+ODLGTEjha5Sf5TeRlOyJ4afd7VOompObwoYeIFd8rYkfMntjlrxHhW0zjoUd6kqCC6NQQS4HKL+Tu7CuS9ew5KgPu58Y++Ghvgn5uJPRUYKAouM9AhAF3sWa6+jgsUCyl7wHivTJs2D8HM/o+A/I9+F+cRw+nmhMX0zch9J2CHk1MZVOl1FPDCdr3MDt2u6ub0EFpZrwR3im7nZ1ZcBaGds3n/UKPUWDpOdTiG1yX9vnfpUSpJLXe1ai1AgZHL9ET8ZwLA5WveZXgYFp4VUm8mC7QsM3t5P8uT8oAuFKVGimwenGtq0r2q462oYPOx2Ag7ruT0O4cQvAptP5buhnBhoHStpgwUJbihK7oueVKzCKaLUz1qk0wv1KzSJmFcyYtQjQz1RP6zM+whz7F7SC+VrjFSu+qXNvvcC8l6/q3njH38FoIZNIDBLVt01Tg2dlnC+g5fWWUsPr4RlztMJpIbaiX9gfFjkE6TZbhxZQKGQs44j6q3/HV2UM+SnXj3slbKf5OKHwkgfzedGrNvQGxeYpKllVnjuhANOGZc+qtYwShv1Vaehz2KMR2Vd6Yb3u5WA20d2rEAWLyrheBoTv8LB23vOxBoaLuV/YgooLMYQQ/HF0mFdbcE+ddJA1y/QIy+X6X0CEfLTICcs9gXCbQocIA9wqesmPaVwq6caf7B0u2Wh1ODatKTV2KMPVyEayl0wz8hTx3t82bMO4lnqgJo6wKQTii/YhcbeVF0AAA",
              "metahash": "1e200dcc58df1635374db14a1b9fb5b8600d0c7ebeb1790e970ba64091dfac2883d2",
              "datahash": "1e20ff3a5975e975de18b0b0438c82bbe91d599cdc4000dae6720ed0bac977669e35"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC6BF6GSH67774G2DH7DTWLFEFCNMT4I4FLLYFPS5X2GFIYNZHLQOA",
              "name": "arena",
              "media_id": "06905h74qu548",
              "content": "http://localhost:8000/api/v1/media/06905h74qu548",
              "mode": "image",
              "filename": "arena.jpg",
              "filesize": 1252407,
              "mediatype": "image/jpeg",
              "width": 3264,
              "height": 2448,
              "thumbnail": "data:image/webp;base64,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",
              "metahash": "1e202951603325fe5fe662de01018c1364402cc0ae7d03d0f5ab7cad2ca8e4ddbd62",
              "datahash": "1e206fa315186e4eb8380a407e1a58bc737e9165f7c8470bb4e44eeb4d65d23d7ece"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECRK4SBOP3GPG6RT6TH5RWATILFCKMN2M6PI7KT6Z6JOSDSZ7WRY6Q",
              "name": "berries",
              "media_id": "06905gmt69tc4",
              "content": "http://localhost:8000/api/v1/media/06905gmt69tc4",
              "mode": "image",
              "filename": "berries.jpg",
              "filesize": 1365293,
              "mediatype": "image/jpeg",
              "width": 4240,
              "height": 2832,
              "thumbnail": "data:image/webp;base64,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",
              "metahash": "1e20b554d04b0ae2eeeee88521c8ac37595a5fb1ed5af9ed4434036d99f4bfa53c31",
              "datahash": "1e207c974872cfed1c7a5fdd464ff880ea3a4cba35d861c4b048e2a367f0fc733889"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC4L75HQI3I5PWF3ZYEHD7QH22GBAJHEUKXS2AJHMKBWALDBZKQNMA",
              "name": "book and phone",
              "media_id": "06905hd8nv0vo",
              "content": "http://localhost:8000/api/v1/media/06905hd8nv0vo",
              "mode": "image",
              "filename": "book-and-phone.jpg",
              "filesize": 491560,
              "mediatype": "image/jpeg",
              "width": 5304,
              "height": 3531,
              "thumbnail": "data:image/webp;base64,UklGRpADAABXRUJQVlA4IIQDAAAwFQCdASqAAFUAPrVQpEqnJSQprVc6STAWiWcA06B+bcSqMib3o3zTVWgz7yzfVJp9s1vBCwZS3UFS3YaThflVwFFwJs8pGD2AQlK12Va4uFGJTLQvAIlDxoaJBh1jB9I5LMlT0seDNvS64XSI49PbK6nlxRoiXzh/q2yXrP5C6y9BgaJiGid5LHvhCJPb6YC2w+evwYegNYunKA1VABg/KlbOFWxoIMyvHqNm0SIsQeDAAP7zls8eYEt2VTY9dwGh1D26riBX+V2CEBHyjb812o0Mnrf0/ek9NdvfFnwN42JOXl9bK4cfTPd4AqRPxCNURgivArqa3Msrl204XqZEXYxLPVhdpT9OFiy0pN/FocWNpZxbr2YtJIER14Pide9i4pJir4I7NUJIElUq1ERQ2jzv84uDqwxY/Agh/dyI6Mw2LFv7HSDA2/uYoetvK+21JUSEX1tWpvnfJs0PGr07qe9JiwAkCKGX0J1T5I9e3626nj7SJPdquTLxX5P2DsBqIsWD5OLOB+41tYGXuStPfJqonDZJKgHR4ezU5fOcB7eU+slQn9atiiIb+thr+0jiR6ARuWBabJElQB5Au0RJCStBkgDVYjkuxY12KAaHwu4wMalr1D0Wyw7b+xf4b6rsALXvhtGtMtY9anpzgD5790zmZXDGgFvVu1ouejPJEoA/9QU0ciWhDScJbpipjB5YVFqvuHllqNWUT5iNvgXf35y1s8IQQBxemosH1Co1GvjvHcDzxGP4ARFWQ4LUJcVof5vGHVcgXjxMnFVFVrz8QOBDfOf4QQlNwy+n2itcV02NFbdPvuumb+vCwhHqOzVqLP9hmGsqXGIZAkJihQilwI7quwnVa0L19LidS6EtIrVssx4XaPMkn0efkBRxYBZovfrt/mh9x6/mH0tffcYuYabJW7y817yDKRWAqXqIsBAgll3NQ5nJCAKuhmLZDzii4v1PTVxL9FISOp8PA8jRNj6S9e7WR0iCNU7Cc72faDNZkCMmu0aTFbersXNgZp9zGKfClS0oRgLyQ6izJhILgM579bCjdyvqnb/YdlHR0Ih1wAdjLZGWHxt2IWb2/v1gUb+pq2p7Hok8vHHFDglSYfWz9HXbEbu2X2cC/tMcli+rGXHPbSARRfEcoc0mAh54RzN+MT6NUyDO1YFxMDIAyVlYppoU615szgk6QBJSnDwJSAA=",
              "metahash": "1e20829e05c1cfae0d1b72756ef12d769530109460e36fa02ce2e108b48bd70c01bf",
              "datahash": "1e20141b01630e5506b089179251fd8d8b150fca173ff0d358d5f7712007d4d4f240"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECSRU7TX527VXG5URIUWJWYOGL47FKMTG6WX77GOL7TUWLV5F254GA",
              "name": "book with pencil",
              "media_id": "06905l3vur3te",
              "content": "http://localhost:8000/api/v1/media/06905l3vur3te",
              "mode": "image",
              "filename": "book-with-pencil.jpg",
              "filesize": 436493,
              "mediatype": "image/jpeg",
              "width": 5184,
              "height": 3416,
              "thumbnail": "data:image/webp;base64,UklGRgoDAABXRUJQVlA4IP4CAABQEwCdASqAAFQAPrVQoEwnJLSqrBVaqpAWiWUG+Bwx7j+A2/88Qb8VCp+r3zS+ztIAYtmClL4R93k7t7NgvQuJub/4CmWjIWUzo4v2mAXwLL/mpatzS5Qe92M5apcFSB2fYMCjGQRWhOwqRz7WrkCkTXIRT1Z1uWyfdbtdfce7o58b86sSarAZ4L3LCOzmwGJNiDli7Ao9QHyhX824SMt0p9cwAP7tQK94cR3QLJuYI8UlQRAFXmkDtoP/JFQhgeuIs4PuxDH3gOrdrr+qyb+ODLGTEjha5Sf5TeRlOyJ4afd7VOompObwoYeIFd8rYkfMntjlrxHhW0zjoUd6kqCC6NQQS4HKL+Tu7CuS9ew5KgPu58Y++Ghvgn5uJPRUYKAouM9AhAF3sWa6+jgsUCyl7wHivTJs2D8HM/o+A/I9+F+cRw+nmhMX0zch9J2CHk1MZVOl1FPDCdr3MDt2u6ub0EFpZrwR3im7nZ1ZcBaGds3n/UKPUWDpOdTiG1yX9vnfpUSpJLXe1ai1AgZHL9ET8ZwLA5WveZXgYFp4VUm8mC7QsM3t5P8uT8oAuFKVGimwenGtq0r2q462oYPOx2Ag7ruT0O4cQvAptP5buhnBhoHStpgwUJbihK7oueVKzCKaLUz1qk0wv1KzSJmFcyYtQjQz1RP6zM+whz7F7SC+VrjFSu+qXNvvcC8l6/q3njH38FoIZNIDBLVt01Tg2dlnC+g5fWWUsPr4RlztMJpIbaiX9gfFjkE6TZbhxZQKGQs44j6q3/HV2UM+SnXj3slbKf5OKHwkgfzedGrNvQGxeYpKllVnjuhANOGZc+qtYwShv1Vaehz2KMR2Vd6Yb3u5WA20d2rEAWLyrheBoTv8LB23vOxBoaLuV/YgooLMYQQ/HF0mFdbcE+ddJA1y/QIy+X6X0CEfLTICcs9gXCbQocIA9wqesmPaVwq6caf7B0u2Wh1ODatKTV2KMPVyEayl0wz8hTx3t82bMO4lnqgJo6wKQTii/YhcbeVF0AAA",
              "metahash": "1e200dcc58df1635374db14a1b9fb5b8600d0c7ebeb1790e970ba64091dfac2883d2",
              "datahash": "1e20ff3a5975e975de18b0b0438c82bbe91d599cdc4000dae6720ed0bac977669e35"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC6BF6GSH67774G2DH7DTWLFEFCNMT4I4FLLYFPS5X2GFIYNZHLQOA",
              "name": "arena",
              "media_id": "06905h74qu548",
              "content": "http://localhost:8000/api/v1/media/06905h74qu548",
              "mode": "image",
              "filename": "arena.jpg",
              "filesize": 1252407,
              "mediatype": "image/jpeg",
              "width": 3264,
              "height": 2448,
              "thumbnail": "data:image/webp;base64,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",
              "metahash": "1e202951603325fe5fe662de01018c1364402cc0ae7d03d0f5ab7cad2ca8e4ddbd62",
              "datahash": "1e206fa315186e4eb8380a407e1a58bc737e9165f7c8470bb4e44eeb4d65d23d7ece"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECRK4SBOP3GPG6RT6TH5RWATILFCKMN2M6PI7KT6Z6JOSDSZ7WRY6Q",
              "name": "berries",
              "media_id": "06905gmt69tc4",
              "content": "http://localhost:8000/api/v1/media/06905gmt69tc4",
              "mode": "image",
              "filename": "berries.jpg",
              "filesize": 1365293,
              "mediatype": "image/jpeg",
              "width": 4240,
              "height": 2832,
              "thumbnail": "data:image/webp;base64,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",
              "metahash": "1e20b554d04b0ae2eeeee88521c8ac37595a5fb1ed5af9ed4434036d99f4bfa53c31",
              "datahash": "1e207c974872cfed1c7a5fdd464ff880ea3a4cba35d861c4b048e2a367f0fc733889"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC4L75HQI3I5PWF3ZYEHD7QH22GBAJHEUKXS2AJHMKBWALDBZKQNMA",
              "name": "book and phone",
              "media_id": "06905hd8nv0vo",
              "content": "http://localhost:8000/api/v1/media/06905hd8nv0vo",
              "mode": "image",
              "filename": "book-and-phone.jpg",
              "filesize": 491560,
              "mediatype": "image/jpeg",
              "width": 5304,
              "height": 3531,
              "thumbnail": "data:image/webp;base64,UklGRpADAABXRUJQVlA4IIQDAAAwFQCdASqAAFUAPrVQpEqnJSQprVc6STAWiWcA06B+bcSqMib3o3zTVWgz7yzfVJp9s1vBCwZS3UFS3YaThflVwFFwJs8pGD2AQlK12Va4uFGJTLQvAIlDxoaJBh1jB9I5LMlT0seDNvS64XSI49PbK6nlxRoiXzh/q2yXrP5C6y9BgaJiGid5LHvhCJPb6YC2w+evwYegNYunKA1VABg/KlbOFWxoIMyvHqNm0SIsQeDAAP7zls8eYEt2VTY9dwGh1D26riBX+V2CEBHyjb812o0Mnrf0/ek9NdvfFnwN42JOXl9bK4cfTPd4AqRPxCNURgivArqa3Msrl204XqZEXYxLPVhdpT9OFiy0pN/FocWNpZxbr2YtJIER14Pide9i4pJir4I7NUJIElUq1ERQ2jzv84uDqwxY/Agh/dyI6Mw2LFv7HSDA2/uYoetvK+21JUSEX1tWpvnfJs0PGr07qe9JiwAkCKGX0J1T5I9e3626nj7SJPdquTLxX5P2DsBqIsWD5OLOB+41tYGXuStPfJqonDZJKgHR4ezU5fOcB7eU+slQn9atiiIb+thr+0jiR6ARuWBabJElQB5Au0RJCStBkgDVYjkuxY12KAaHwu4wMalr1D0Wyw7b+xf4b6rsALXvhtGtMtY9anpzgD5790zmZXDGgFvVu1ouejPJEoA/9QU0ciWhDScJbpipjB5YVFqvuHllqNWUT5iNvgXf35y1s8IQQBxemosH1Co1GvjvHcDzxGP4ARFWQ4LUJcVof5vGHVcgXjxMnFVFVrz8QOBDfOf4QQlNwy+n2itcV02NFbdPvuumb+vCwhHqOzVqLP9hmGsqXGIZAkJihQilwI7quwnVa0L19LidS6EtIrVssx4XaPMkn0efkBRxYBZovfrt/mh9x6/mH0tffcYuYabJW7y817yDKRWAqXqIsBAgll3NQ5nJCAKuhmLZDzii4v1PTVxL9FISOp8PA8jRNj6S9e7WR0iCNU7Cc72faDNZkCMmu0aTFbersXNgZp9zGKfClS0oRgLyQ6izJhILgM579bCjdyvqnb/YdlHR0Ih1wAdjLZGWHxt2IWb2/v1gUb+pq2p7Hok8vHHFDglSYfWz9HXbEbu2X2cC/tMcli+rGXHPbSARRfEcoc0mAh54RzN+MT6NUyDO1YFxMDIAyVlYppoU615szgk6QBJSnDwJSAA=",
              "metahash": "1e20829e05c1cfae0d1b72756ef12d769530109460e36fa02ce2e108b48bd70c01bf",
              "datahash": "1e20141b01630e5506b089179251fd8d8b150fca173ff0d358d5f7712007d4d4f240"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECSRU7TX527VXG5URIUWJWYOGL47FKMTG6WX77GOL7TUWLV5F254GA",
              "name": "book with pencil",
              "media_id": "06905l3vur3te",
              "content": "http://localhost:8000/api/v1/media/06905l3vur3te",
              "mode": "image",
              "filename": "book-with-pencil.jpg",
              "filesize": 436493,
              "mediatype": "image/jpeg",
              "width": 5184,
              "height": 3416,
              "thumbnail": "data:image/webp;base64,UklGRgoDAABXRUJQVlA4IP4CAABQEwCdASqAAFQAPrVQoEwnJLSqrBVaqpAWiWUG+Bwx7j+A2/88Qb8VCp+r3zS+ztIAYtmClL4R93k7t7NgvQuJub/4CmWjIWUzo4v2mAXwLL/mpatzS5Qe92M5apcFSB2fYMCjGQRWhOwqRz7WrkCkTXIRT1Z1uWyfdbtdfce7o58b86sSarAZ4L3LCOzmwGJNiDli7Ao9QHyhX824SMt0p9cwAP7tQK94cR3QLJuYI8UlQRAFXmkDtoP/JFQhgeuIs4PuxDH3gOrdrr+qyb+ODLGTEjha5Sf5TeRlOyJ4afd7VOompObwoYeIFd8rYkfMntjlrxHhW0zjoUd6kqCC6NQQS4HKL+Tu7CuS9ew5KgPu58Y++Ghvgn5uJPRUYKAouM9AhAF3sWa6+jgsUCyl7wHivTJs2D8HM/o+A/I9+F+cRw+nmhMX0zch9J2CHk1MZVOl1FPDCdr3MDt2u6ub0EFpZrwR3im7nZ1ZcBaGds3n/UKPUWDpOdTiG1yX9vnfpUSpJLXe1ai1AgZHL9ET8ZwLA5WveZXgYFp4VUm8mC7QsM3t5P8uT8oAuFKVGimwenGtq0r2q462oYPOx2Ag7ruT0O4cQvAptP5buhnBhoHStpgwUJbihK7oueVKzCKaLUz1qk0wv1KzSJmFcyYtQjQz1RP6zM+whz7F7SC+VrjFSu+qXNvvcC8l6/q3njH38FoIZNIDBLVt01Tg2dlnC+g5fWWUsPr4RlztMJpIbaiX9gfFjkE6TZbhxZQKGQs44j6q3/HV2UM+SnXj3slbKf5OKHwkgfzedGrNvQGxeYpKllVnjuhANOGZc+qtYwShv1Vaehz2KMR2Vd6Yb3u5WA20d2rEAWLyrheBoTv8LB23vOxBoaLuV/YgooLMYQQ/HF0mFdbcE+ddJA1y/QIy+X6X0CEfLTICcs9gXCbQocIA9wqesmPaVwq6caf7B0u2Wh1ODatKTV2KMPVyEayl0wz8hTx3t82bMO4lnqgJo6wKQTii/YhcbeVF0AAA",
              "metahash": "1e200dcc58df1635374db14a1b9fb5b8600d0c7ebeb1790e970ba64091dfac2883d2",
              "datahash": "1e20ff3a5975e975de18b0b0438c82bbe91d599cdc4000dae6720ed0bac977669e35"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC6BF6GSH67774G2DH7DTWLFEFCNMT4I4FLLYFPS5X2GFIYNZHLQOA",
              "name": "arena",
              "media_id": "06905h74qu548",
              "content": "http://localhost:8000/api/v1/media/06905h74qu548",
              "mode": "image",
              "filename": "arena.jpg",
              "filesize": 1252407,
              "mediatype": "image/jpeg",
              "width": 3264,
              "height": 2448,
              "thumbnail": "data:image/webp;base64,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",
              "metahash": "1e202951603325fe5fe662de01018c1364402cc0ae7d03d0f5ab7cad2ca8e4ddbd62",
              "datahash": "1e206fa315186e4eb8380a407e1a58bc737e9165f7c8470bb4e44eeb4d65d23d7ece"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECRK4SBOP3GPG6RT6TH5RWATILFCKMN2M6PI7KT6Z6JOSDSZ7WRY6Q",
              "name": "berries",
              "media_id": "06905gmt69tc4",
              "content": "http://localhost:8000/api/v1/media/06905gmt69tc4",
              "mode": "image",
              "filename": "berries.jpg",
              "filesize": 1365293,
              "mediatype": "image/jpeg",
              "width": 4240,
              "height": 2832,
              "thumbnail": "data:image/webp;base64,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",
              "metahash": "1e20b554d04b0ae2eeeee88521c8ac37595a5fb1ed5af9ed4434036d99f4bfa53c31",
              "datahash": "1e207c974872cfed1c7a5fdd464ff880ea3a4cba35d861c4b048e2a367f0fc733889"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC4L75HQI3I5PWF3ZYEHD7QH22GBAJHEUKXS2AJHMKBWALDBZKQNMA",
              "name": "book and phone",
              "media_id": "06905hd8nv0vo",
              "content": "http://localhost:8000/api/v1/media/06905hd8nv0vo",
              "mode": "image",
              "filename": "book-and-phone.jpg",
              "filesize": 491560,
              "mediatype": "image/jpeg",
              "width": 5304,
              "height": 3531,
              "thumbnail": "data:image/webp;base64,UklGRpADAABXRUJQVlA4IIQDAAAwFQCdASqAAFUAPrVQpEqnJSQprVc6STAWiWcA06B+bcSqMib3o3zTVWgz7yzfVJp9s1vBCwZS3UFS3YaThflVwFFwJs8pGD2AQlK12Va4uFGJTLQvAIlDxoaJBh1jB9I5LMlT0seDNvS64XSI49PbK6nlxRoiXzh/q2yXrP5C6y9BgaJiGid5LHvhCJPb6YC2w+evwYegNYunKA1VABg/KlbOFWxoIMyvHqNm0SIsQeDAAP7zls8eYEt2VTY9dwGh1D26riBX+V2CEBHyjb812o0Mnrf0/ek9NdvfFnwN42JOXl9bK4cfTPd4AqRPxCNURgivArqa3Msrl204XqZEXYxLPVhdpT9OFiy0pN/FocWNpZxbr2YtJIER14Pide9i4pJir4I7NUJIElUq1ERQ2jzv84uDqwxY/Agh/dyI6Mw2LFv7HSDA2/uYoetvK+21JUSEX1tWpvnfJs0PGr07qe9JiwAkCKGX0J1T5I9e3626nj7SJPdquTLxX5P2DsBqIsWD5OLOB+41tYGXuStPfJqonDZJKgHR4ezU5fOcB7eU+slQn9atiiIb+thr+0jiR6ARuWBabJElQB5Au0RJCStBkgDVYjkuxY12KAaHwu4wMalr1D0Wyw7b+xf4b6rsALXvhtGtMtY9anpzgD5790zmZXDGgFvVu1ouejPJEoA/9QU0ciWhDScJbpipjB5YVFqvuHllqNWUT5iNvgXf35y1s8IQQBxemosH1Co1GvjvHcDzxGP4ARFWQ4LUJcVof5vGHVcgXjxMnFVFVrz8QOBDfOf4QQlNwy+n2itcV02NFbdPvuumb+vCwhHqOzVqLP9hmGsqXGIZAkJihQilwI7quwnVa0L19LidS6EtIrVssx4XaPMkn0efkBRxYBZovfrt/mh9x6/mH0tffcYuYabJW7y817yDKRWAqXqIsBAgll3NQ5nJCAKuhmLZDzii4v1PTVxL9FISOp8PA8jRNj6S9e7WR0iCNU7Cc72faDNZkCMmu0aTFbersXNgZp9zGKfClS0oRgLyQ6izJhILgM579bCjdyvqnb/YdlHR0Ih1wAdjLZGWHxt2IWb2/v1gUb+pq2p7Hok8vHHFDglSYfWz9HXbEbu2X2cC/tMcli+rGXHPbSARRfEcoc0mAh54RzN+MT6NUyDO1YFxMDIAyVlYppoU615szgk6QBJSnDwJSAA=",
              "metahash": "1e20829e05c1cfae0d1b72756ef12d769530109460e36fa02ce2e108b48bd70c01bf",
              "datahash": "1e20141b01630e5506b089179251fd8d8b150fca173ff0d358d5f7712007d4d4f240"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECSRU7TX527VXG5URIUWJWYOGL47FKMTG6WX77GOL7TUWLV5F254GA",
              "name": "book with pencil",
              "media_id": "06905l3vur3te",
              "content": "http://localhost:8000/api/v1/media/06905l3vur3te",
              "mode": "image",
              "filename": "book-with-pencil.jpg",
              "filesize": 436493,
              "mediatype": "image/jpeg",
              "width": 5184,
              "height": 3416,
              "thumbnail": "data:image/webp;base64,UklGRgoDAABXRUJQVlA4IP4CAABQEwCdASqAAFQAPrVQoEwnJLSqrBVaqpAWiWUG+Bwx7j+A2/88Qb8VCp+r3zS+ztIAYtmClL4R93k7t7NgvQuJub/4CmWjIWUzo4v2mAXwLL/mpatzS5Qe92M5apcFSB2fYMCjGQRWhOwqRz7WrkCkTXIRT1Z1uWyfdbtdfce7o58b86sSarAZ4L3LCOzmwGJNiDli7Ao9QHyhX824SMt0p9cwAP7tQK94cR3QLJuYI8UlQRAFXmkDtoP/JFQhgeuIs4PuxDH3gOrdrr+qyb+ODLGTEjha5Sf5TeRlOyJ4afd7VOompObwoYeIFd8rYkfMntjlrxHhW0zjoUd6kqCC6NQQS4HKL+Tu7CuS9ew5KgPu58Y++Ghvgn5uJPRUYKAouM9AhAF3sWa6+jgsUCyl7wHivTJs2D8HM/o+A/I9+F+cRw+nmhMX0zch9J2CHk1MZVOl1FPDCdr3MDt2u6ub0EFpZrwR3im7nZ1ZcBaGds3n/UKPUWDpOdTiG1yX9vnfpUSpJLXe1ai1AgZHL9ET8ZwLA5WveZXgYFp4VUm8mC7QsM3t5P8uT8oAuFKVGimwenGtq0r2q462oYPOx2Ag7ruT0O4cQvAptP5buhnBhoHStpgwUJbihK7oueVKzCKaLUz1qk0wv1KzSJmFcyYtQjQz1RP6zM+whz7F7SC+VrjFSu+qXNvvcC8l6/q3njH38FoIZNIDBLVt01Tg2dlnC+g5fWWUsPr4RlztMJpIbaiX9gfFjkE6TZbhxZQKGQs44j6q3/HV2UM+SnXj3slbKf5OKHwkgfzedGrNvQGxeYpKllVnjuhANOGZc+qtYwShv1Vaehz2KMR2Vd6Yb3u5WA20d2rEAWLyrheBoTv8LB23vOxBoaLuV/YgooLMYQQ/HF0mFdbcE+ddJA1y/QIy+X6X0CEfLTICcs9gXCbQocIA9wqesmPaVwq6caf7B0u2Wh1ODatKTV2KMPVyEayl0wz8hTx3t82bMO4lnqgJo6wKQTii/YhcbeVF0AAA",
              "metahash": "1e200dcc58df1635374db14a1b9fb5b8600d0c7ebeb1790e970ba64091dfac2883d2",
              "datahash": "1e20ff3a5975e975de18b0b0438c82bbe91d599cdc4000dae6720ed0bac977669e35"
            }
            ]
        """.trimIndent()
        val shortIsccData = """
        [
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KEC4L75HQI3I5PWF3ZYEHD7QH22GBAJHEUKXS2AJHMKBWALDBZKQNMA",
              "name": "book and phone",
              "media_id": "06905hd8nv0vo",
              "content": "http://localhost:8000/api/v1/media/06905hd8nv0vo",
              "mode": "image",
              "filename": "book-and-phone.jpg",
              "filesize": 491560,
              "mediatype": "image/jpeg",
              "width": 5304,
              "height": 3531,
              "thumbnail": "data:image/webp;base64,UklGRpADAABXRUJQVlA4IIQDAAAwFQCdASqAAFUAPrVQpEqnJSQprVc6STAWiWcA06B+bcSqMib3o3zTVWgz7yzfVJp9s1vBCwZS3UFS3YaThflVwFFwJs8pGD2AQlK12Va4uFGJTLQvAIlDxoaJBh1jB9I5LMlT0seDNvS64XSI49PbK6nlxRoiXzh/q2yXrP5C6y9BgaJiGid5LHvhCJPb6YC2w+evwYegNYunKA1VABg/KlbOFWxoIMyvHqNm0SIsQeDAAP7zls8eYEt2VTY9dwGh1D26riBX+V2CEBHyjb812o0Mnrf0/ek9NdvfFnwN42JOXl9bK4cfTPd4AqRPxCNURgivArqa3Msrl204XqZEXYxLPVhdpT9OFiy0pN/FocWNpZxbr2YtJIER14Pide9i4pJir4I7NUJIElUq1ERQ2jzv84uDqwxY/Agh/dyI6Mw2LFv7HSDA2/uYoetvK+21JUSEX1tWpvnfJs0PGr07qe9JiwAkCKGX0J1T5I9e3626nj7SJPdquTLxX5P2DsBqIsWD5OLOB+41tYGXuStPfJqonDZJKgHR4ezU5fOcB7eU+slQn9atiiIb+thr+0jiR6ARuWBabJElQB5Au0RJCStBkgDVYjkuxY12KAaHwu4wMalr1D0Wyw7b+xf4b6rsALXvhtGtMtY9anpzgD5790zmZXDGgFvVu1ouejPJEoA/9QU0ciWhDScJbpipjB5YVFqvuHllqNWUT5iNvgXf35y1s8IQQBxemosH1Co1GvjvHcDzxGP4ARFWQ4LUJcVof5vGHVcgXjxMnFVFVrz8QOBDfOf4QQlNwy+n2itcV02NFbdPvuumb+vCwhHqOzVqLP9hmGsqXGIZAkJihQilwI7quwnVa0L19LidS6EtIrVssx4XaPMkn0efkBRxYBZovfrt/mh9x6/mH0tffcYuYabJW7y817yDKRWAqXqIsBAgll3NQ5nJCAKuhmLZDzii4v1PTVxL9FISOp8PA8jRNj6S9e7WR0iCNU7Cc72faDNZkCMmu0aTFbersXNgZp9zGKfClS0oRgLyQ6izJhILgM579bCjdyvqnb/YdlHR0Ih1wAdjLZGWHxt2IWb2/v1gUb+pq2p7Hok8vHHFDglSYfWz9HXbEbu2X2cC/tMcli+rGXHPbSARRfEcoc0mAh54RzN+MT6NUyDO1YFxMDIAyVlYppoU615szgk6QBJSnDwJSAA=",
              "metahash": "1e20829e05c1cfae0d1b72756ef12d769530109460e36fa02ce2e108b48bd70c01bf",
              "datahash": "1e20141b01630e5506b089179251fd8d8b150fca173ff0d358d5f7712007d4d4f240"
            },
            {
              "@context": "http://purl.org/iscc/context/0.4.0.jsonld",
              "@type": "ImageObject",
              "schema": "http://purl.org/iscc/schema/0.4.0.json",
              "iscc": "ISCC:KECSRU7TX527VXG5URIUWJWYOGL47FKMTG6WX77GOL7TUWLV5F254GA",
              "name": "book with pencil",
              "media_id": "06905l3vur3te",
              "content": "http://localhost:8000/api/v1/media/06905l3vur3te",
              "mode": "image",
              "filename": "book-with-pencil.jpg",
              "filesize": 436493,
              "mediatype": "image/jpeg",
              "width": 5184,
              "height": 3416,
              "thumbnail": "data:image/webp;base64,UklGRgoDAABXRUJQVlA4IP4CAABQEwCdASqAAFQAPrVQoEwnJLSqrBVaqpAWiWUG+Bwx7j+A2/88Qb8VCp+r3zS+ztIAYtmClL4R93k7t7NgvQuJub/4CmWjIWUzo4v2mAXwLL/mpatzS5Qe92M5apcFSB2fYMCjGQRWhOwqRz7WrkCkTXIRT1Z1uWyfdbtdfce7o58b86sSarAZ4L3LCOzmwGJNiDli7Ao9QHyhX824SMt0p9cwAP7tQK94cR3QLJuYI8UlQRAFXmkDtoP/JFQhgeuIs4PuxDH3gOrdrr+qyb+ODLGTEjha5Sf5TeRlOyJ4afd7VOompObwoYeIFd8rYkfMntjlrxHhW0zjoUd6kqCC6NQQS4HKL+Tu7CuS9ew5KgPu58Y++Ghvgn5uJPRUYKAouM9AhAF3sWa6+jgsUCyl7wHivTJs2D8HM/o+A/I9+F+cRw+nmhMX0zch9J2CHk1MZVOl1FPDCdr3MDt2u6ub0EFpZrwR3im7nZ1ZcBaGds3n/UKPUWDpOdTiG1yX9vnfpUSpJLXe1ai1AgZHL9ET8ZwLA5WveZXgYFp4VUm8mC7QsM3t5P8uT8oAuFKVGimwenGtq0r2q462oYPOx2Ag7ruT0O4cQvAptP5buhnBhoHStpgwUJbihK7oueVKzCKaLUz1qk0wv1KzSJmFcyYtQjQz1RP6zM+whz7F7SC+VrjFSu+qXNvvcC8l6/q3njH38FoIZNIDBLVt01Tg2dlnC+g5fWWUsPr4RlztMJpIbaiX9gfFjkE6TZbhxZQKGQs44j6q3/HV2UM+SnXj3slbKf5OKHwkgfzedGrNvQGxeYpKllVnjuhANOGZc+qtYwShv1Vaehz2KMR2Vd6Yb3u5WA20d2rEAWLyrheBoTv8LB23vOxBoaLuV/YgooLMYQQ/HF0mFdbcE+ddJA1y/QIy+X6X0CEfLTICcs9gXCbQocIA9wqesmPaVwq6caf7B0u2Wh1ODatKTV2KMPVyEayl0wz8hTx3t82bMO4lnqgJo6wKQTii/YhcbeVF0AAA",
              "metahash": "1e200dcc58df1635374db14a1b9fb5b8600d0c7ebeb1790e970ba64091dfac2883d2",
              "datahash": "1e20ff3a5975e975de18b0b0438c82bbe91d599cdc4000dae6720ed0bac977669e35"
            }
        ]
        """.trimIndent()
        return largeIsccData
    }
    
}