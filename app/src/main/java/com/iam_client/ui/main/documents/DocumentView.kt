package com.iam_client.ui.main.documents

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.iam_client.R
import com.iam_client.repostories.data.docs.*
import com.iam_client.ui.utils.Formatter.formatMoneyString
import com.iam_client.viewModels.main.documents.*
import kotlinx.android.synthetic.main.fragment_document_view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.text.DateFormat
import com.iam_client.utills.reactive.observeEvent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.toast


class DocumentView : Fragment(), KodeinAware {

    override val kodein by closestKodein()

    enum class DocType { Invoice, Order, Quotation, CreditNote, DeliveryNote }

    private val viewModel by lazy {
        val factory = when (navArgs<DocumentViewArgs>().value.docType) {
            Document.Type.Invoice -> { val f: InvoiceViewModelFactory by instance();f }
            Document.Type.CreditNote -> { val f: CreditNoteViewModelFactory by instance();f }
            Document.Type.Order -> { val f: OrderViewModelFactory by instance();f }
            Document.Type.Quotation -> { val f: QuotationViewModelFactory by instance();f }
            Document.Type.DeliveryNote -> { val f: DeliveryNoteViewModelFactory by instance();f }
        }
        factory.docSn = navArgs<DocumentViewArgs>().value.docSn
        factory.docType = when (navArgs<DocumentViewArgs>().value.docType) {
            Document.Type.Invoice ->  Invoice::class
            Document.Type.CreditNote -> CreditNote::class
            Document.Type.Order -> Order::class
            Document.Type.Quotation -> Quotation::class
            Document.Type.DeliveryNote -> DeliveryNote::class
        }
        ViewModelProviders
            .of(this, factory)
            .get(DocumentViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.document.observe(viewLifecycleOwner, Observer { if (it != null) bindDocument(it) })
        viewModel.salesman.observe(viewLifecycleOwner, Observer { salesmanTextView.setText(it?.name) })
        viewModel.errorEvent.observeEvent(this) { toast("error: ${it.message}") }
        viewModel.refreshing.observe(this) {
            loading_progress_bar.visibility = when (it) {
                true -> VISIBLE
                else -> GONE
            }
        }
        viewModel.itemLoading.observe(this) {
            itemLoadingProgressBar.visibility = when (it) {
                true -> VISIBLE
                else -> GONE
            }
        }

        viewModel.isEditable.observe(this, Observer {
            edit_Fb.visibility = when (it) {
                true -> VISIBLE
                false -> GONE
            }
        })
        viewModel.isCancelable.observe(this, Observer {
            cancel_button.visibility = when (it) {
                true -> VISIBLE
                false -> GONE
            }
        })

        edit_Fb.onClick { viewModel.editDocument() }
        cancel_button.onClick { viewModel.cancelDocument() }

        viewModel.editDocumentEvent.observeEvent(this){
            findNavController().navigate(
                DocumentViewDirections.actionDocumentViewToDocumentEditMode(editDocSN = it.sn?:-1)
            )
        }
    }


    @SuppressLint("SetTextI18n")
    private fun bindDocument(document: Document) {
        document.apply {
            snField.setText(sn!!.toString())
            cardNameTextField.setText(customerName)
            cardCodeTextField.setText(customerSN)
            if (date != null)
                dueDateField.setText(DateFormat.getDateInstance().format(date))
            else
                dueDateField.setText("")

            totalBeforeDCTextField.setText(totalBeforeVatAndDiscount.formatMoneyString(currency))
            dcTextField.setText(totalDiscountAndRounding.formatMoneyString(currency))
            vatTestField.setText(vat?.formatMoneyString(currency))
            totalTestField.setText(docTotal?.formatMoneyString(currency))
            totalPaidSum.setText(paid.formatMoneyString(currency))
            totalNeedToPay.setText(needToPay.formatMoneyString(currency))
            commentsTextField.setText(comments)

            isCanceledIcon.visibility = when (isCanceled) {
                true -> VISIBLE
                false -> GONE
            }
            isCloseIcon.visibility = when (isClosed) {
                true -> VISIBLE
                false -> GONE
            }

            val tempItems = items
            if (tempItems != null) {
                item_records_list.removeAllViews() //TODO refresh only if the list has changed
                tempItems.forEach { item ->
                    val itemView = DocItemView(this@DocumentView.context!!)
                    itemView.item = item
                    item_records_list.addView(itemView)
                }
            }

        }

    }
}
