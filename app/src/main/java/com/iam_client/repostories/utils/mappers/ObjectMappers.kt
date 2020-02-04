package com.iam_client.repostories.utils.mappers

import com.iam_client.local.data.*
import com.iam_client.local.data.docs.*
import com.iam_client.local.data.products.ProductEntity
import com.iam_client.local.data.products.ProductCategoryEntity
import com.iam_client.local.data.products.ProductPropertyEntity
import com.iam_client.remote.data.*
import com.iam_client.remote.data.docs.*
import com.iam_client.remote.data.products.ProductDTO
import com.iam_client.remote.data.products.ProductCategoryDTO
import com.iam_client.remote.data.products.ProductPropertyDTO
import com.iam_client.repostories.data.*
import com.iam_client.repostories.data.docs.*
import com.iam_client.repostories.data.products.Product
import com.iam_client.repostories.data.products.ProductCategory
import com.iam_client.repostories.data.products.ProductProperty
import java.util.*


/***
 *  Objects Mappers
 */
fun CustomerEntity.mapToModel(cardGroups: Map<Int, CardGroup>, salesmen: Map<Int, Salesman>) =
    Customer(
        cid = cid,
        name = name,
        group = cardGroups.getValue(groupSN),
        isActive = isActive,
        balance = balance,
        currency = currency,
        federalTaxID = federalTaxID,
        type = when (type) {
            CustomerEntity.Types.Company -> Customer.Type.Company
            CustomerEntity.Types.Employee -> Customer.Type.Employee
            CustomerEntity.Types.Private -> Customer.Type.Private
        },
        billingAddress = billingAddress?.mapToModel(),
        shippingAddress = shippingAddress?.mapToModel(),
        cellular = cellular ?: "",
        comments = comments ?: "",
        email = email ?: "",
        fax = fax ?: "",
        phone1 = phone1 ?: "",
        phone2 = phone2 ?: "",
        salesman = salesmen.getValue(salesmanCode)
    )

fun Customer.mapToDTO() = CustomerDTO(
    cid = cid,
    name = name,
    groupSN = group.sn,
    isActive = isActive,
    balance = balance,
    currency = currency,
    federalTaxID = federalTaxID,
    type = when (type) {
        Customer.Type.Company -> CustomerDTO.Types.Company
        Customer.Type.Employee -> CustomerDTO.Types.Employee
        Customer.Type.Private -> CustomerDTO.Types.Private
    },
    billingAddress = billingAddress?.mapToDTO(),
    shippingAddress = shippingAddress?.mapToDTO(),
    cellular = cellular,
    comments = comments,
    email = email,
    fax = fax,
    phone1 = phone1,
    phone2 = phone2,
    salesmanCode = salesman.sn,
    lastUpdateDateTime = null,
    creationDateTime = null
)


fun CustomerDTO.mapToEntity() = CustomerEntity(
    cid = cid!!,
    name = name ?: "",
    groupSN = groupSN!!,
    isActive = isActive!!,
    balance = balance,
    currency = currency ?: "",
    federalTaxID = federalTaxID ?: "",
    type = when (type!!) {
        CustomerDTO.Types.Company -> CustomerEntity.Types.Company
        CustomerDTO.Types.Employee -> CustomerEntity.Types.Employee
        CustomerDTO.Types.Private -> CustomerEntity.Types.Private

    },
    lastUpdateDateTime = lastUpdateDateTime,
    creationDateTime = creationDateTime!!,//entity must be created
    billingAddress = billingAddress?.mapToEntity(),
    shippingAddress = shippingAddress?.mapToEntity(),
    cellular = cellular,
    comments = comments,
    email = email,
    fax = fax,
    phone1 = phone1,
    phone2 = phone2,
    salesmanCode = salesmanCode!!
)

fun AddressDTO.mapToEntity() = AddressEntity(
    id = id ?: "",
    country = country ?: "",
    city = city ?: "",
    block = block ?: "",
    street = street ?: "",
    numAtStreet = numAtStreet ?: "",
    apartment = apartment ?: "",
    zipCode = zipCode ?: ""
)

fun AddressEntity.mapToModel() = Address(
    id = id,
    country = country ?: "",
    city = city ?: "",
    block = block ?: "",
    street = street ?: "",
    numAtStreet = numAtStreet ?: "",
    apartment = apartment ?: "",
    zipCode = zipCode ?: ""
)


fun Address.mapToDTO() = AddressDTO(
    id = id,
    country = country,
    city = city,
    block = block,
    street = street,
    numAtStreet = numAtStreet,
    apartment = apartment,
    zipCode = zipCode
)

fun CardGroupDTO.mapToEntity() = CardGroupEntity(sn = sn, name = name)

fun CardGroupEntity.mapToModel() = CardGroup(sn = sn, name = name)

fun CustomerBalanceRecordDTO.mapToEntity() = CustomerBalanceRecordEntity(
    sn = sn,
    balanceDebt = balanceDebt,
    date = date,
    debt = debt,
    currency = currency ?: "",
    doc1SN = doc1SN,
    doc2SN = doc2SN,
    memo = memo,
    ownerSN = ownerSN,
    type = when (type) {
        CustomerBalanceRecordDTO.Type.CreditInvoice -> CustomerBalanceRecordEntity.Type.CreditInvoice
        CustomerBalanceRecordDTO.Type.Invoice -> CustomerBalanceRecordEntity.Type.Invoice
        CustomerBalanceRecordDTO.Type.Receipt -> CustomerBalanceRecordEntity.Type.Receipt
        CustomerBalanceRecordDTO.Type.Journal -> CustomerBalanceRecordEntity.Type.Journal
        CustomerBalanceRecordDTO.Type.Other -> CustomerBalanceRecordEntity.Type.Other

    }
)

fun CustomerBalanceRecordEntity.mapToModel() = CustomerBalanceRecord(
    sn = sn,
    balanceDebt = balanceDebt,
    date = date.mapToDate(),
    debt = debt,
    currency = currency,
    doc1SN = doc1SN,
    doc2SN = doc2SN,
    memo = memo,
    ownerSN = ownerSN,
    type = when (type) {
        CustomerBalanceRecordEntity.Type.CreditInvoice -> CustomerBalanceRecord.Type.CreditInvoice
        CustomerBalanceRecordEntity.Type.Invoice -> CustomerBalanceRecord.Type.Invoice
        CustomerBalanceRecordEntity.Type.Receipt -> CustomerBalanceRecord.Type.Receipt
        CustomerBalanceRecordEntity.Type.Journal -> CustomerBalanceRecord.Type.Journal
        CustomerBalanceRecordEntity.Type.Other -> CustomerBalanceRecord.Type.Other
    }
)


fun SalesmanDTO.mapToEntity() = SalesmanEntity(
    sn = sn,
    name = name,
    mobile = mobile,
    email = email,
    isActive = isActive
)

fun SalesmanEntity.mapToModel() = Salesman(
    sn = sn,
    name = name ?: "",
    mobile = mobile,
    email = email,
    isActive = isActive
)

fun DocItemDTO.mapToEntity() = DocItemEntity(
    code = code,
    comments = comments,
    currency = currency,
    description = description,
    details = details,
    discountPercent = discountPercent,
    docNumber = docNumber,
    isOpen = isOpen,
    itemNumber = itemNumber,
    pricePerQuantity = pricePerQuantity,
    quantity = quantity,
    openQuantity = openQuantity,
    properties = properties,
    baseDoc = baseDoc?.mapToEntity(),
    baseItemNumber = baseItemNumber,
    followDoc = followDoc?.mapToEntity(),
    visualOrder = visualOrder
)

fun DocItemEntity.mapToModel() = DocItem(
    code = code,
    comments = comments,
    currency = currency,
    description = description,
    details = details,
    discountPercent = discountPercent!!,
    docNumber = docNumber,
    isOpen = isOpen!!,
    itemNumber = itemNumber,
    pricePerQuantity = pricePerQuantity!!,
    quantity = quantity!!,
    properties = properties?.toMutableMap() ?: mutableMapOf(),
    baseDoc = baseDoc?.mapToModel(),
    baseItemNumber = baseItemNumber,
    followDoc = followDoc?.mapToModel(),
    visualOrder = visualOrder,
    openQuantity = openQuantity
)

fun DocItemDTO.DocReferenceDTO.mapToEntity() = DocItemEntity.DocReferenceEntity(
    docNumber = docNumber,
    docType = when (docType) {
        DocItemDTO.DocType.CreditNote -> DocItemEntity.DocType.CreditNote
        DocItemDTO.DocType.DeliveryNote -> DocItemEntity.DocType.DeliveryNote
        DocItemDTO.DocType.Invoice -> DocItemEntity.DocType.Invoice
        DocItemDTO.DocType.Quotation -> DocItemEntity.DocType.Quotation
        DocItemDTO.DocType.DepositNote -> DocItemEntity.DocType.DepositNote
        DocItemDTO.DocType.Order -> DocItemEntity.DocType.Order
        DocItemDTO.DocType.Other -> DocItemEntity.DocType.Other
        DocItemDTO.DocType.Receipt -> DocItemEntity.DocType.Recipt
    }
)

fun DocItemEntity.DocReferenceEntity.mapToModel() = DocItem.DocReference(
    docNumber = docNumber,
    docType = when (docType) {
        DocItemEntity.DocType.CreditNote -> DocItem.DocType.CreditNote
        DocItemEntity.DocType.DeliveryNote -> DocItem.DocType.DeliveryNote
        DocItemEntity.DocType.Invoice -> DocItem.DocType.Invoice
        DocItemEntity.DocType.Quotation -> DocItem.DocType.Quotation
        DocItemEntity.DocType.DepositNote -> DocItem.DocType.DepositNote
        DocItemEntity.DocType.Order -> DocItem.DocType.Order
        DocItemEntity.DocType.Other -> DocItem.DocType.Other
        DocItemEntity.DocType.Recipt -> DocItem.DocType.Recipt
    }
)

fun DocumentDTO.mapToEntity(entity: DocumentEntity) = entity.let {

    it.externalSN = externalSN
    it.customerSN = customerSN ?: ""
    it.customerName = customerName ?: ""
    it.customerAddress = customerAddress ?: ""
    it.customerFederalTaxID = customerFederalTaxID
    it.date = date
    it.closingDate = closingDateTime
    it.creationDateTime = creationDateTime
    it.comments = comments
    it.currency = currency
    it.discountPercent = discountPercent
    it.docTotal = docTotal
    it.isCanceled = isCanceled
    it.isClosed = isClosed
    it.items = items?.map { itm -> itm.mapToEntity() }
    it.ownerEmployeeSN = ownerEmployeeSN
    it.salesmanSN = salesmanSN
    it.totalDiscountAndRounding = totalDiscountAndRounding
    it.vat = vat
    it.vatPercent = vatPercent
    it.paid = paid ?: 0.0
    it.grosProfit = grosProfit
    it.lastUpdateDateTime = lastUpdateDateTime
}

fun InvoiceDTO.mapToEntity() = InvoiceEntity(sn!!).apply {
    mapToEntity(this)//map documents properties
}

fun CreditNoteDTO.mapToEntity() = CreditNoteEntity(sn!!).apply {
    mapToEntity(this)//map documents properties
}

fun DeliveryNoteDTO.mapToEntity() = DeliveryNoteEntity(sn!!).apply {
    mapToEntity(this)//map documents properties
    supplyDate = this@mapToEntity.supplyDate
}

fun OrderDTO.mapToEntity() = OrderEntity(sn!!).apply {
    mapToEntity(this)//map documents properties
    supplyDate = this@mapToEntity.supplyDate
}

fun QuotationDTO.mapToEntity() = QuotationEntity(sn!!).apply {
    mapToEntity(this)//map documents properties
    validUntil = this@mapToEntity.validUntil
}

fun DocumentEntity.mapToModel(entity: Document) = entity.let {
    it.externalSN = externalSN
    it.customerSN = customerSN
    it.customerName = customerName
    it.customerAddress = customerAddress
    it.customerFederalTaxID = customerFederalTaxID
    it.date = date.mapToDate()
    it.closingDate = closingDate.mapToDate()
    it.creationDate = creationDateTime.mapToDate()

    it.comments = comments
    it.currency = currency
    it.discountPercent = discountPercent
    it.docTotal = docTotal
    it.isCanceled = isCanceled ?: false
    it.isClosed = isClosed ?: false
    it.items = items?.map { itm -> itm.mapToModel() }
    it.ownerEmployeeSN = ownerEmployeeSN
    it.salesmanSN = salesmanSN
    it.totalDiscountAndRounding = totalDiscountAndRounding ?: 0.0
    it.vatPercent = vatPercent
    it.vat = vat
    it.paid = paid
    it.grossProfit = grosProfit
}

fun InvoiceEntity.mapToModel() = Invoice(sn).apply {
    mapToModel(this)//map documents properties
}

fun CreditNoteEntity.mapToModel() = CreditNote(sn).apply {
    mapToModel(this)//map documents properties
}

fun DeliveryNoteEntity.mapToModel() = DeliveryNote(sn).apply {
    mapToModel(this)//map documents properties
    this.supplyDate = this@mapToModel.supplyDate.mapToDate()
}

fun OrderEntity.mapToModel() = Order(sn).apply {
    mapToModel(this)//map documents properties
    this.supplyDate = this@mapToModel.supplyDate.mapToDate()
}

fun QuotationEntity.mapToModel() = Quotation(sn).apply {
    mapToModel(this)//map documents properties
    this.validUntil = this@mapToModel.validUntil.mapToDate()
}


fun Document.mapToDTO(documentDTO: DocumentDTO) = documentDTO.let {
    it.sn = sn
    it.externalSN = externalSN
    it.customerSN = customerSN
    it.customerName = customerName
    it.customerAddress = customerAddress
    it.customerFederalTaxID = customerFederalTaxID
    it.date = date?.mapToUnixTS()
    it.closingDateTime = closingDate?.mapToUnixTS()
    it.creationDateTime = creationDate?.mapToUnixTS()
    it.comments = comments
    it.currency = currency
    it.discountPercent = discountPercent
    it.docTotal = docTotal
    it.isCanceled = isCanceled
    it.isClosed = isClosed
    it.items = items?.map { itm -> itm.mapToDTO() }
    it.ownerEmployeeSN = ownerEmployeeSN
    it.salesmanSN = salesmanSN
    it.totalDiscountAndRounding = totalDiscountAndRounding
    it.vat = vat
    it.vatPercent = vatPercent
    it.paid = paid
    it.grosProfit = grossProfit
}

fun Invoice.mapToDTO() = InvoiceDTO().apply {
    mapToDTO(this)//map documents properties
}

fun CreditNote.mapToDTO() = CreditNoteDTO().apply {
    mapToDTO(this)//map documents properties
}

fun DeliveryNote.mapToDTO() = DeliveryNoteDTO().apply {
    mapToDTO(this)//map documents properties
    this.supplyDate = this@mapToDTO.supplyDate?.mapToUnixTS()
}

fun Order.mapToDTO() = OrderDTO().apply {
    mapToDTO(this)//map documents properties
    this.supplyDate = this@mapToDTO.supplyDate?.mapToUnixTS()
}

fun Quotation.mapToDTO() = QuotationDTO().apply {
    mapToDTO(this)//map documents properties
    this.validUntil = this@mapToDTO.validUntil?.mapToUnixTS()
}


fun DocItem.mapToDTO() = DocItemDTO(
    code = code,
    comments = comments,
    currency = currency,
    description = description,
    details = details,
    discountPercent = discountPercent,
    docNumber = docNumber,
    isOpen = isOpen,
    itemNumber = itemNumber,
    pricePerQuantity = pricePerQuantity,
    quantity = quantity,
    openQuantity = openQuantity,
    properties = properties,
    baseDoc = baseDoc?.mapToDTO(),
    baseItemNumber = baseItemNumber,
    followDoc = followDoc?.mapToDTO(),
    visualOrder = visualOrder
)

fun DocItem.DocReference.mapToDTO() = DocItemDTO.DocReferenceDTO(
    docNumber = docNumber,
    docType = when (docType) {
        DocItem.DocType.CreditNote -> DocItemDTO.DocType.CreditNote
        DocItem.DocType.DeliveryNote -> DocItemDTO.DocType.DeliveryNote
        DocItem.DocType.Invoice -> DocItemDTO.DocType.Invoice
        DocItem.DocType.Quotation -> DocItemDTO.DocType.Quotation
        DocItem.DocType.DepositNote -> DocItemDTO.DocType.DepositNote
        DocItem.DocType.Order -> DocItemDTO.DocType.Order
        DocItem.DocType.Other -> DocItemDTO.DocType.Other
        DocItem.DocType.Recipt -> DocItemDTO.DocType.Receipt
    }
)

fun Long?.mapToDate() = when (val date = this) {
    null -> null
    else -> date.mapToDate()
}

fun Long.mapToDate() = Date(this * 1000)


fun Date.mapToUnixTS(): Long = this.time / 1000

fun ProductCategoryDTO.mapToEntity(): ProductCategoryEntity = ProductCategoryEntity(
    code = code,
    name = name ?: "",
    pictureURL = pictureURL,
    creationDate = creationDate,
    lastUpdateDate = lastUpdateDate
)

fun ProductCategoryEntity.mapToModel(): ProductCategory = ProductCategory(
    code = code,
    name = name,
    pictureURL = pictureURL
)


fun ProductPropertyDTO.mapToEntity() = ProductPropertyEntity(
    code = code,
    type = when (type) {
        ProductPropertyDTO.PropertyType.Choice -> ProductPropertyEntity.PropertyType.Choice
        ProductPropertyDTO.PropertyType.Date -> ProductPropertyEntity.PropertyType.Date
        ProductPropertyDTO.PropertyType.Decimal -> ProductPropertyEntity.PropertyType.Decimal
        ProductPropertyDTO.PropertyType.Int -> ProductPropertyEntity.PropertyType.Int
        ProductPropertyDTO.PropertyType.Text -> ProductPropertyEntity.PropertyType.Text
    },
    minValue = minValue,
    maxValue = maxValue,
    choices = choices,
    fromDate = fromDate,
    toDate = toDate,
    maxDaysFromNow = maxDaysFromNow,
    uom = uom,
    defaultValue = defaultValue
)

fun ProductPropertyEntity.mapToModel() = ProductProperty(
    code = code,
    type = when (type) {
        ProductPropertyEntity.PropertyType.Choice -> ProductProperty.PropertyType.Choice
        ProductPropertyEntity.PropertyType.Date -> ProductProperty.PropertyType.Date
        ProductPropertyEntity.PropertyType.Decimal -> ProductProperty.PropertyType.Decimal
        ProductPropertyEntity.PropertyType.Int -> ProductProperty.PropertyType.Int
        ProductPropertyEntity.PropertyType.Text -> ProductProperty.PropertyType.Text
    },
    minValue = minValue,
    maxValue = maxValue,
    choices = choices,
    fromDate = fromDate,
    toDate = toDate,
    maxDaysFromNow = maxDaysFromNow,
    uom = uom,
    defaultValue = when (type) {
        ProductPropertyEntity.PropertyType.Int -> (defaultValue as? Double)?.toInt()
        else -> defaultValue

    }

)

fun ProductDTO.mapToEntity(): ProductEntity = ProductEntity(
    code = code,
    groupCode = groupCode,
    name = name ?: "",
    nameForegin = nameForegin ?: "",
    barcode = barcode,
    pictureURL = pictureURL,
    isActive = isActive,
    isForSell = isForSell,
    isForBuy = isForBuy,
    properties = properties?.map { it.mapToEntity() }?.toList() ?: listOf(),
    creationDateTime = creationDateTime,
    lastUpdateDateTime = lastUpdateDateTime,
    autoQuantityCalculationExpression = autoQuantityCalculationExpression
)

fun ProductEntity.mapToModel(): Product = Product(
    code = code,
    groupCode = groupCode,
    name = name,
    nameForegin = nameForegin,
    barcode = barcode,
    pictureURL = pictureURL,
    isActive = isActive,
    isForSell = isForSell,
    isForBuy = isForBuy,
    properties = properties.map { it.mapToModel() }.toList(),
    autoQuantityCalculationExpression = autoQuantityCalculationExpression
)


fun EmployeeDTO.mapToEntity() = EmployeeEntity(
    sn = sn!!,
    firstName = firstName,
    middleName = middleName,
    lastName = lastName,
    id = id,
    gender =  when(gender){
        EmployeeDTO.Genders.Female->EmployeeEntity.Genders.Female
        EmployeeDTO.Genders.Male->EmployeeEntity.Genders.Male
    },
    birthday = birthday,
    isActive = isActive,
    homePhone = homePhone,
    officePhone = officePhone,
    workCellular = workCellular,
    fax = fax,
    department = department?.mapToEntity(),
    position = position?.mapToEntity(),
    managerSN = managerSN,
    salesmanSN = salesmanSN,
    homeAddress = homeAddress?.mapToEntity(),
    workAddress = workAddress?.mapToEntity(),
    picUrl = picPath
)

fun EmployeeDTO.JobPositionDTO.mapToEntity() =
    EmployeeEntity.JobPositionEntity(
        code =  code!!,
        name = name,
        description = description
    )

fun EmployeeDTO.DepartmentDTO.mapToEntity() =
    EmployeeEntity.DepartmentEntity(
        code =  code!!,
        name = name,
        description = description
    )

fun EmployeeEntity.mapToModel() = Employee(
    sn = sn,
    firstName = firstName,
    middleName = middleName,
    lastName = lastName,
    id = id,
    gender =  when(gender){
       EmployeeEntity.Genders.Female->Employee.Genders.Female
       EmployeeEntity.Genders.Male->Employee.Genders.Male
    },
    birthday = birthday,
    isActive = isActive,
    homePhone = homePhone,
    officePhone = officePhone,
    workCellular = workCellular,
    fax = fax,
    department = department?.mapToModel(),
    position = position?.mapToModel(),
    managerSN = managerSN,
    salesmanSN = salesmanSN,
    homeAddress = homeAddress?.mapToModel(),
    workAddress = workAddress?.mapToModel(),
    picUrl = picUrl
)

fun EmployeeEntity.DepartmentEntity.mapToModel() =
    Employee.Department(
        code =  code,
        name = name,
        description = description
    )

fun EmployeeEntity.JobPositionEntity.mapToModel() =
    Employee.JobPosition(
        code =  code,
        name = name,
        description = description
    )





