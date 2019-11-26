package dm.uporov.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import d.uporov.list.di.injectListPresenter
import dm.uporov.machete.annotation.Inject

class ListActivity : AppCompatActivity(), ListView {

    @get:Inject
    val listPresenter: ListPresenter by injectListPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun showItems(items: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}