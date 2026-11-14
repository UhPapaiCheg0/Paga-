package com.pagae.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pagae.app.R
import com.pagae.app.db.Food
import com.pagae.app.db.HistoryRow
import com.pagae.app.db.Person
import com.pagae.app.db.TotalRow

/** Lista de pessoas com modo de seleção múltipla para exclusão. */
class PersonEditAdapter(
    private var items: List<Person>,
    private val onClick: (Person) -> Unit
) : RecyclerView.Adapter<PersonEditAdapter.VH>() {

    var selectionMode = false
        set(value) {
            field = value
            if (!value) checked.clear()
            notifyDataSetChanged()
        }
    val checked = mutableSetOf<Long>()

    fun update(newItems: List<Person>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val contact: TextView = view.findViewById(R.id.txtContact)
        val check: CheckBox = view.findViewById(R.id.chkSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.name.text = p.name
        holder.contact.text = if (p.contact.isBlank()) "Sem contato" else "Contato: ${p.contact}"
        holder.check.visibility = if (selectionMode) View.VISIBLE else View.GONE
        holder.check.setOnCheckedChangeListener(null)
        holder.check.isChecked = checked.contains(p.id)
        holder.check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checked.add(p.id) else checked.remove(p.id)
        }
        holder.itemView.setOnClickListener { onClick(p) }
    }
}

/** Lista de comidas com modo de seleção múltipla para exclusão. */
class FoodEditAdapter(
    private var items: List<Food>,
    private val onClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodEditAdapter.VH>() {

    var selectionMode = false
        set(value) {
            field = value
            if (!value) checked.clear()
            notifyDataSetChanged()
        }
    val checked = mutableSetOf<Long>()

    fun update(newItems: List<Food>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val price: TextView = view.findViewById(R.id.txtPrice)
        val check: CheckBox = view.findViewById(R.id.chkSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val f = items[position]
        holder.name.text = if (f.emoji.isBlank()) f.name else f.emoji + " " + f.name
        holder.price.text = formatMoney(f.price)
        holder.check.visibility = if (selectionMode) View.VISIBLE else View.GONE
        holder.check.setOnCheckedChangeListener(null)
        holder.check.isChecked = checked.contains(f.id)
        holder.check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checked.add(f.id) else checked.remove(f.id)
        }
        holder.itemView.setOnClickListener { onClick(f) }
    }
}

/** Lista de pessoas para seleção única (tela de venda) — contorno dourado quando selecionado. */
class PersonPickAdapter(
    private var items: List<Person>,
    private val compactGrid: Boolean = false,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<PersonPickAdapter.VH>() {

    private var selectedId: Long? = null

    fun update(newItems: List<Person>) {
        items = newItems
        selectedId = null
        notifyDataSetChanged()
    }

    fun selectedPerson(): Person? = items.find { it.id == selectedId }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val contact: TextView = view.findViewById(R.id.txtContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.name.text = p.name
        holder.name.textSize = if (compactGrid) 13f else 18f
        holder.name.maxLines = 2
        holder.contact.text = if (p.contact.isBlank()) "" else "Contato: ${p.contact}"
        holder.contact.textSize = if (compactGrid) 10f else 14f
        holder.contact.maxLines = 2
        holder.itemView.minimumHeight = if (compactGrid) 88 else 0
        val selected = p.id == selectedId
        holder.itemView.setBackgroundResource(
            if (selected) R.drawable.bg_card_selected else R.drawable.bg_card
        )
        holder.itemView.setOnClickListener {
            selectedId = if (selectedId == p.id) null else p.id
            notifyDataSetChanged()
            onSelectionChanged()
        }
    }
}

/** Lista de comidas para seleção única (tela de venda) — contorno dourado quando selecionado. */
class FoodPickAdapter(
    private var items: List<Food>,
    private val compactGrid: Boolean = false,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<FoodPickAdapter.VH>() {

    private var selectedId: Long? = null

    fun update(newItems: List<Food>) {
        items = newItems
        selectedId = null
        notifyDataSetChanged()
    }

    fun selectedFood(): Food? = items.find { it.id == selectedId }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val price: TextView = view.findViewById(R.id.txtPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val f = items[position]
        holder.name.text = if (f.emoji.isBlank()) f.name else f.emoji + " " + f.name
        holder.name.textSize = if (compactGrid) 13f else 18f
        holder.name.maxLines = 2
        holder.price.text = formatMoney(f.price)
        holder.price.textSize = if (compactGrid) 11f else 14f
        holder.itemView.minimumHeight = if (compactGrid) 88 else 0
        val selected = f.id == selectedId
        holder.itemView.setBackgroundResource(
            if (selected) R.drawable.bg_card_selected else R.drawable.bg_card
        )
        holder.itemView.setOnClickListener {
            selectedId = if (selectedId == f.id) null else f.id
            notifyDataSetChanged()
            onSelectionChanged()
        }
    }
}

/** Lista de pessoas com saldo acumulado (página Totais e Limpar saldos). */
class TotalsAdapter(
    private var items: List<TotalRow>,
    private val onClick: (TotalRow) -> Unit
) : RecyclerView.Adapter<TotalsAdapter.VH>() {

    fun update(newItems: List<TotalRow>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val total: TextView = view.findViewById(R.id.txtTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_total, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        holder.name.text = t.name
        holder.total.text = formatMoney(t.total)
        holder.itemView.setOnClickListener { onClick(t) }
    }
}

/** Histórico de compras de um perfil. */
class HistoryAdapter(private var items: List<HistoryRow>) :
    RecyclerView.Adapter<HistoryAdapter.VH>() {

    fun update(newItems: List<HistoryRow>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.txtDate)
        val food: TextView = view.findViewById(R.id.txtFood)
        val price: TextView = view.findViewById(R.id.txtPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val h = items[position]
        holder.date.text = h.date
        holder.food.text = h.food
        holder.price.text = formatMoney(h.price)
    }
}
