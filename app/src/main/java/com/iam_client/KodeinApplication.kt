package com.iam_client

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.iam_client.local.LocalDB
import com.iam_client.local.dao.CustomerDao
import com.iam_client.local.dao.EmployeeDao
import com.iam_client.local.dao.SalesmanDao
import com.iam_client.local.dao.TranslationDao
import com.iam_client.local.dao.docs.*
import com.iam_client.local.dao.products.ProductsDao
import com.iam_client.remote.apiServices.CustomerApiService
import com.iam_client.remote.interceptors.TokenInterceptor
import com.iam_client.remote.apiServices.LoginApiService
import com.iam_client.remote.interceptors.TimeoutInterceptor
import com.iam_client.remote.apiServices.EmployeeApiService
import com.iam_client.remote.apiServices.SalesmenApiService
import com.iam_client.remote.apiServices.docs.*
import com.iam_client.remote.apiServices.products.ProductsApiService
import com.iam_client.repostories.customer.CustomerRepository
import com.iam_client.repostories.customer.CustomerRepositoryImpl
import com.iam_client.repostories.data.docs.*
import com.iam_client.repostories.documents.DocumentRepository
import com.iam_client.repostories.documents.IQuotationRepository
import com.iam_client.repostories.documents.impl.*
import com.iam_client.repostories.employee.IEmployeeRepository
import com.iam_client.repostories.employee.EmployeeRepository
import com.iam_client.repostories.products.IProductRepository
import com.iam_client.repostories.products.ProductsRepository
import com.iam_client.repostories.salesmen.SalesmenRepository
import com.iam_client.repostories.salesmen.SalesmenRepositoryImpl
import com.iam_client.repostories.utils.settings.SettingsProvider
import com.iam_client.repostories.utils.settings.SettingsProviderImpl
import com.iam_client.utills.StringProvider
import com.iam_client.local.secure.Encryption
import com.iam_client.local.secure.ISecureStoring
import com.iam_client.local.secure.SecureStoring
import com.iam_client.remote.apiServices.utils.AuthorizationService
import com.iam_client.remote.apiServices.utils.IAuthorizationService
import com.iam_client.remote.apiServices.utils.IAuthorizationWebService
import com.iam_client.repostories.user.IUserRepository
import com.iam_client.repostories.user.UserRepository
import com.iam_client.remote.apiServices.googleApi.ITranslateService
import com.iam_client.remote.apiServices.googleApi.TranslateService
import com.iam_client.viewModels.factories.CustomerListViewModelFactory
import com.iam_client.viewModels.login.LoginFormViewModelFactory
import com.iam_client.viewModels.main.CatalogViewModelFactory
import com.iam_client.viewModels.main.MainEntryViewModelFactory
import com.iam_client.viewModels.main.customers.BalanceHistoryViewModelFactory
import com.iam_client.viewModels.main.customers.CustomerCardEditModeModelFactory
import com.iam_client.viewModels.main.customers.CustomerCardViewModelFactory
import com.iam_client.viewModels.main.customers.SetAddressSharedViewModelFactory
import com.iam_client.viewModels.main.documents.*
import com.iam_client.viewModels.main.documents.InvoiceViewModelFactory
import com.iam_client.viewModels.main.salesman.SalesmanDocumentsViewModelFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KodeinApplication : Application(), KodeinAware {
    private val settingsProvider: SettingsProvider by instance()

    //Web Services
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(applicationContext))
            .addInterceptor(TimeoutInterceptor())
            .build()
    }
    private val retrofit: Retrofit by lazy {
        val temp = Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())//enable suspend functions instead of calls
        if (settingsProvider.useCustomURL)
            temp.baseUrl(settingsProvider.serverURL!!)
        else
            temp.baseUrl("http://server_ip:server_port/api/")
        temp.build()
    }

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.setDefaultValues(this, R.xml.preference, false)
        Encryption.initSecretKey()

        GlobalScope.launch {
            if (settingsProvider.clearCacheOnStart) {
                val userRepository : IUserRepository by instance()
                userRepository.logout()

                val customerDao: CustomerDao by instance()
                val employeeDao:EmployeeDao by instance()
                val salesmanDao: SalesmanDao by instance()
                val quotationDao: QuotationDao by instance()
                val orderDao: OrderDao by instance()
                val invoiceDao: InvoiceDao by instance()
                val deliveryNoteD: DeliveryNoteDao by instance()
                val creditNoteDao: CreditNoteDao by instance()
                val productsDao: ProductsDao by instance()


                employeeDao.deleteAll()
                customerDao.deleteAll()
                customerDao.deleteAllCardGroups()
                salesmanDao.deleteAll()
                quotationDao.deleteAll()
                orderDao.deleteAll()
                invoiceDao.deleteAll()
                deliveryNoteD.deleteAll()
                creditNoteDao.deleteAll()
                productsDao.deleteAll()
                productsDao.deleteAllCategories()
            }

            try {
                val customerRepository: CustomerRepository by instance()
                val productsRepository: IProductRepository by instance()
                withContext(Dispatchers.IO) {
                    awaitAll(
                        async { customerRepository.refreshCustomers() },
                        async { productsRepository.refreshProductsAndCategories() }
                    )
                }
            } catch (error: Throwable) {
                Log.e("Application startup", "error: ${error.message}")
            }
        }
    }

    override val kodein = Kodein.lazy {
        import(androidXModule(this@KodeinApplication))
        bind<StringProvider>() with singleton { StringProvider(this@KodeinApplication) }

        bind<LoginApiService>() with singleton { retrofit.create(LoginApiService::class.java) }
        bind<CustomerApiService>() with singleton { retrofit.create(CustomerApiService::class.java) }
        bind<EmployeeApiService>() with singleton { retrofit.create(EmployeeApiService::class.java) }
        bind<SalesmenApiService>() with singleton { retrofit.create(SalesmenApiService::class.java) }
        bind<InvoiceService>() with singleton { retrofit.create(InvoiceService::class.java) }
        bind<OrderService>() with singleton { retrofit.create(OrderService::class.java) }
        bind<QuotationService>() with singleton { retrofit.create(QuotationService::class.java) }
        bind<DeliveryNoteService>() with singleton { retrofit.create(DeliveryNoteService::class.java) }
        bind<CreditNoteService>() with singleton { retrofit.create(CreditNoteService::class.java) }

        bind<ProductsApiService>() with singleton { retrofit.create(ProductsApiService::class.java) }

        //Local DB DAOs
        bind<TranslationDao>() with singleton { LocalDB.getInstance(applicationContext).translationDao() }

        bind<CustomerDao>() with singleton { LocalDB.getInstance(applicationContext).customerDao() }
        bind<EmployeeDao>() with singleton { LocalDB.getInstance(applicationContext).employeeDao() }
        bind<SalesmanDao>() with singleton { LocalDB.getInstance(applicationContext).salesmanDao() }
        bind<QuotationDao>() with singleton {
            LocalDB.getInstance(applicationContext).quotationDao()
        }
        bind<OrderDao>() with singleton { LocalDB.getInstance(applicationContext).orderDao() }
        bind<InvoiceDao>() with singleton { LocalDB.getInstance(applicationContext).invoiceDao() }
        bind<DeliveryNoteDao>() with singleton {
            LocalDB.getInstance(applicationContext).deliveryNoteDao()
        }
        bind<CreditNoteDao>() with singleton {
            LocalDB.getInstance(applicationContext).creditNoteDao()
        }

        bind<ProductsDao>() with singleton { LocalDB.getInstance(applicationContext).productsDao() }

        //Repositories
        bind<CustomerRepository>() with singleton {
            CustomerRepositoryImpl.getInstance(
                instance(), instance(), instance()
            )
        }
        bind<SalesmenRepository>() with singleton {
            SalesmenRepositoryImpl.getInstance(
                instance(),
                instance()
            )
        }
        bind<IEmployeeRepository>() with singleton {
            EmployeeRepository.getInstance(
                instance(),
                instance()
            )
        }
        bind<IQuotationRepository>() with singleton {
            QuotationRepository.getInstance(
                instance(),
                instance()
            )
        }

        bind<DocumentRepository<Invoice>>() with singleton {
            InvoiceRepositoryImpl.getInstance(
                instance(),
                instance()
            )
        }
        bind<DocumentRepository<Quotation>>() with singleton {
            QuotationRepository.getInstance(
                instance(), instance()
            )
        }
        bind<DocumentRepository<CreditNote>>() with singleton {
            CreditNoteRepositoryImpl.getInstance(
                instance(), instance()
            )
        }
        bind<DocumentRepository<DeliveryNote>>() with singleton {
            DeliveryNoteRepositoryImpl.getInstance(
                instance(), instance()
            )
        }
        bind<DocumentRepository<Order>>() with singleton {
            OrderRepositoryImpl.getInstance(
                instance(),
                instance()
            )
        }
        bind<IProductRepository>() with singleton {
            ProductsRepository.getInstance(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        bind<IUserRepository>() with singleton { UserRepository.getInstance(instance()) }


        //ViewModels Factories
        bind<LoginFormViewModelFactory>() with singleton { LoginFormViewModelFactory(instance()) }
        bind<MainEntryViewModelFactory>() with singleton { MainEntryViewModelFactory(instance(),instance()) }

        bind<CustomerListViewModelFactory>() with singleton { CustomerListViewModelFactory(instance()) }
        bind<CustomerCardViewModelFactory>() with singleton { CustomerCardViewModelFactory(instance()) }
        bind<CustomerCardEditModeModelFactory>() with singleton {
            CustomerCardEditModeModelFactory(instance(), instance(), instance())
        }
        bind<BalanceHistoryViewModelFactory>() with singleton {
            BalanceHistoryViewModelFactory(
                instance()
            )
        }
        bind<SetAddressSharedViewModelFactory>() with singleton { SetAddressSharedViewModelFactory() }


        bind<InvoiceViewModelFactory>() with singleton {
            InvoiceViewModelFactory(
                instance(),
                instance()
            )
        }
        bind<CreditNoteViewModelFactory>() with singleton {
            CreditNoteViewModelFactory(
                instance(),
                instance()
            )
        }
        bind<OrderViewModelFactory>() with singleton {
            OrderViewModelFactory(
                instance(),
                instance()
            )
        }
        bind<QuotationViewModelFactory>() with singleton {
            QuotationViewModelFactory(
                instance(),
                instance()
            )
        }
        bind<DeliveryNoteViewModelFactory>() with singleton {
            DeliveryNoteViewModelFactory(
                instance(),
                instance()
            )
        }

        bind<CatalogViewModelFactory>() with singleton { CatalogViewModelFactory(instance()) }

        bind<SalesmanDocumentsViewModelFactory>() with singleton {
            SalesmanDocumentsViewModelFactory(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        bind<CustomerDocumentsTabsViewModelFactory>() with singleton {
            CustomerDocumentsTabsViewModelFactory(
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        bind<EditDocumentModeModelFactory>() with singleton {
            EditDocumentModeModelFactory(
                instance(),
                instance(),
                instance()
            )
        }
        bind<EditDocItemPropertiedViewModelFactory>() with singleton {
            EditDocItemPropertiedViewModelFactory(
                instance()
            )
        }


        //Security Services
        bind<ISecureStoring>() with singleton { SecureStoring(applicationContext) }

        //Others
        bind<SettingsProvider>() with singleton { SettingsProviderImpl(instance()) }

        //Authorization Services
        bind<IAuthorizationService>() with singleton {
            AuthorizationService.getInstance(
                instance(),
                instance()
            )
        }
        bind<IAuthorizationWebService>() with singleton {
            AuthorizationService.getInstance(
                instance(),
                instance()
            )
        }
        bind<ITranslateService>() with singleton {
            TranslateService(
                this@KodeinApplication
            )
        }



    }
}


