package com.iam_client
import com.iam_client.remote.data.CustomerDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.web.client.RestTemplate
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter


/**
 * Example local unit test, which will handleResult on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun loadCustomerDTO(){
        val URL = "http://10.0.0.71:52652/api/Customer/GetByID/21070"
        val restTemplate = RestTemplate()
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
        val response = restTemplate.getForEntity(URL, CustomerDTO::class.java)
        val x = 0

    }


}
