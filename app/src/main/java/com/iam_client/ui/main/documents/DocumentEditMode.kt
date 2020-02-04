package com.iam_client.ui.main.documents


import android.app.DatePickerDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

import com.iam_client.R
import com.iam_client.databinding.FragmentDocumentEditModeBinding
import com.iam_client.repostories.data.docs.*
import com.iam_client.ui.main.CatalogList
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.main.documents.EditDocumentModeModelFactory
import com.iam_client.viewModels.main.documents.EditDocumentViewModel
import kotlinx.android.synthetic.main.fragment_document_edit_mode.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import com.iam_client.ui.utils.Formatter.formatString
import com.iam_client.ui.utils.Formatter.formatMoneyString
import com.iam_client.utills.reactive.collectionsLiveData.MutableListLiveData
import com.iam_client.utills.reactive.collectionsLiveData.ObservableCollectionsAction
import kotlin.reflect.KClass
import java.util.*


class DocumentEditMode : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val docType: KClass<out Document> = Quotation::class
    private var isUpdateMode : Boolean =false
    private lateinit var binding: FragmentDocumentEditModeBinding
    private val viewModel by lazy {
        val modelFactory: EditDocumentModeModelFactory by instance()
        modelFactory.customer = navArgs<DocumentEditModeArgs>().value.customer
        modelFactory.docType =docType//TODO
        val editDocSN =  navArgs<DocumentEditModeArgs>().value.editDocSN
        isUpdateMode = editDocSN!=-1
        modelFactory.docSN = if(isUpdateMode)editDocSN else null
        ViewModelProviders
            .of(this, modelFactory)
            .get(EditDocumentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_document_edit_mode,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel


        viewModel.header.observe(this, Observer {
            it?.apply {
                snField.setText("${sn ?: ""}")
                cardNameTextField.setText(customerName)
                cardCodeTextField.setText(customerSN)
                docDateField.setText(date?.formatString())

                totalBeforeDCTextField.setText(totalBeforeVatAndDiscount.formatMoneyString(currency))
                dcTextField.setText(totalDiscountAndRounding.formatMoneyString(currency))
                vatTestField.setText(vat?.formatMoneyString(currency))
                totalTestField.setText(docTotal?.formatMoneyString(currency))
                totalPaidSum.setText(paid.formatMoneyString(currency))
                totalNeedToPay.setText(needToPay.formatMoneyString(currency))
                commentsTextField.setText(comments)

                isCanceledIcon.visibility = when (isCanceled) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
                isCloseIcon.visibility = when (isClosed) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
            }

        })

        val itemsAdapter = ItemListAdapter(viewModel.items, this)
        ItemTouchHelper(
            SwipToRemoveItemTouchHelperCallback(
                itemsAdapter, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ).apply {
                icon = context!!.getDrawable(R.drawable.delete_black)
            }
        ).attachToRecyclerView(item_records_list)

        item_records_list.apply {
            adapter = itemsAdapter
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@DocumentEditMode.context)
        }



        add_item_btn.onClick {
            val catalogList = CatalogList()
            catalogList.onProductClicked = { viewModel.addNewItem(it); catalogList.dismiss() }
            catalogList.show(childFragmentManager, "tag")
        }

        viewModel.openDocItemEditDialog.observeEvent(this) {
            val dialog = EditDocItemPropertiesDialog(it)
            //add the item to the ViewModel
            dialog.onConfirm = { editedItem ->
                viewModel.addNewItem(editedItem)
                dialog.dismiss()

            }

            dialog.show(this@DocumentEditMode.requireFragmentManager(), "${it.itemNumber ?: 0}")
        }

        viewModel.errorMessage.observeEvent(this) { toast("${it.message}") }

        viewModel.loading.observe(this, Observer {
            progressBar.visibility = when (it) {
                true -> View.VISIBLE
                false -> View.GONE
            }
        })

        viewModel.docSaved.observeEvent(this){
            if(isUpdateMode)
                findNavController().popBackStack()
            else {

                findNavController().navigate(
                    DocumentEditModeDirections.actionDocumentEditModeToDocumentView(
                        it.sn!!,
                        it.type
                    )
                )
            }
        }







        docDueDateHintText.hint = when (docType) {
            Quotation::class -> context?.getString(R.string.doc_valid_until_date)
            Order::class -> context?.getString(R.string.supply_date)
            DeliveryNote::class -> context?.getString(R.string.supply_date)
            Invoice::class -> context?.getString(R.string.doc_due_date)
            CreditNote::class -> context?.getString(R.string.doc_due_date)
            else -> "TODO name the field"
        }


        docDueDateField.onClick {
            val myCalendar = Calendar.getInstance()
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                docDueDateField.tag = myCalendar.time
                docDueDateField.setText(myCalendar.time?.formatString())
            }
            DatePickerDialog(
                this@DocumentEditMode.context!!, dateListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }

    class ItemListAdapter(val dosItems: MutableListLiveData<DocItem>, owner: LifecycleOwner) :
        RecyclerView.Adapter<ItemListAdapter.ItemListRecordVH>(), IItemRemovableAdapter {

        init {
            dosItems.observeEvent(owner) {
                when (it.type) {
                    ObservableCollectionsAction.Type.AddAll -> notifyDataSetChanged()
                    ObservableCollectionsAction.Type.Add -> notifyItemInserted(it.index!!)
                    ObservableCollectionsAction.Type.AddElement -> notifyItemInserted(
                        it.index!!
                    )
                    ObservableCollectionsAction.Type.Remove -> notifyItemRemoved(it.index!!)
                    ObservableCollectionsAction.Type.RemoveAt -> notifyItemRemoved(it.index!!)
                    else -> Log.e(
                        "DocEditMode-View ",
                        "Item lost operation not implemented!! ${it.type}"
                    )
                }
            }
        }

        override fun removeItem(viewHolder: RecyclerView.ViewHolder) {
            val removedItem = dosItems.getList()[viewHolder.adapterPosition]
            val removedPos = viewHolder.adapterPosition
            dosItems.removeAt(viewHolder.adapterPosition)
            Snackbar.make(viewHolder.itemView, "item deleted.", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    dosItems.add(removedPos, removedItem)
                }.show()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListRecordVH {
            return ItemListRecordVH(DocItemView(parent.context))
        }

        override fun getItemCount(): Int = dosItems.getList().size

        override fun onBindViewHolder(holder: ItemListRecordVH, position: Int) {
            holder.docItemView.item = dosItems.getList()[position]
        }

        class ItemListRecordVH(val docItemView: DocItemView) : RecyclerView.ViewHolder(docItemView)
    }


    interface IItemRemovableAdapter {
        fun removeItem(viewHolder: RecyclerView.ViewHolder)
    }

    class SwipToRemoveItemTouchHelperCallback(
        private val adapter: IItemRemovableAdapter, swipeDirs: Int
    ) : ItemTouchHelper.SimpleCallback(0, swipeDirs) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false //items cant be moved
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter.removeItem(viewHolder)
        }

        var icon: Drawable? = null
        var swipedBackgroundDrawable: ColorDrawable =
            ColorDrawable(Color.parseColor("#FF0000"))

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val iconVerticalMargin = (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2
            val iconHorizontalMargin = 10

            data class Bound(val left: Int, val top: Int, val right: Int, val bottom: Int)

            val swipedBackgroundBound: Bound
            if (dX > 0) {
                //user swiped right
                swipedBackgroundBound =
                    Bound(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                icon?.setBounds(
                    itemView.left + iconHorizontalMargin,
                    itemView.top + iconVerticalMargin,
                    itemView.left + iconHorizontalMargin + icon!!.intrinsicWidth,
                    itemView.bottom - iconVerticalMargin
                )

            } else {
                swipedBackgroundBound = Bound(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                //user swiped left
                icon?.setBounds(
                    itemView.right - iconHorizontalMargin - icon!!.intrinsicWidth,
                    itemView.top + iconVerticalMargin,
                    itemView.right - iconHorizontalMargin,
                    itemView.bottom - iconVerticalMargin
                )
            }
            swipedBackgroundDrawable.setBounds(
                swipedBackgroundBound.left,
                swipedBackgroundBound.top,
                swipedBackgroundBound.right,
                swipedBackgroundBound.bottom
            )
            swipedBackgroundDrawable.draw(c)

            c.save()
            c.clipRect(
                swipedBackgroundBound.left,
                swipedBackgroundBound.top,
                swipedBackgroundBound.right,
                swipedBackgroundBound.bottom
            )
            icon?.draw(c)
            c.restore()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }


}
