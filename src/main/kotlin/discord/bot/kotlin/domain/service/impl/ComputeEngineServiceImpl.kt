package discord.bot.kotlin.domain.service.impl

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.compute.Compute
import com.google.api.services.compute.ComputeScopes
import com.google.api.services.compute.model.Instance
import com.google.api.services.compute.model.InstanceList
import com.google.api.services.compute.model.Operation
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import discord.bot.kotlin.domain.service.ComputeEngineService
import java.io.IOException
import java.util.*


class ComputeEngineServiceImpl : ComputeEngineService {
  private var httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
  private var compute: Compute
  private var httpRequestInitializer: HttpRequestInitializer

  init {
    // Authenticate using Google Application Default Credentials.
    var credential = GoogleCredentials.getApplicationDefault()
    if (credential.createScopedRequired()) {
      val scopes: MutableList<String> = ArrayList()
      // Set Google Cloud Storage scope to Full Control.
      scopes.add(ComputeScopes.DEVSTORAGE_FULL_CONTROL)
      // Set Google Compute Engine scope to Read-write.
      scopes.add(ComputeScopes.COMPUTE)
      credential = credential.createScoped(scopes)
    }
    httpRequestInitializer = HttpCredentialsAdapter(credential)
    compute = Compute.Builder(
      httpTransport,
      JSON_FACTORY, httpRequestInitializer
    )
      .setApplicationName(APPLICATION_NAME)
      .build()
  }

  override fun startInstance(): String {
    try {
      // List out instances, looking for the one created by this sample app.
      val foundOurInstance: Boolean = printInstances(compute)
      if (!foundOurInstance) {
        throw Exception("instance is not found.")
      }
      // execute start instance
      val startOperation = compute.instances().start(
        PROJECT_ID,
        ZONE_NAME,
        APPLICATION_NAME
      )
      val operation = startOperation.execute()


      val error =
        blockUntilComplete(
          compute, operation,
          OPERATION_TIMEOUT_MILLIS
        )
      return if (error == null) "Success!" else error.toPrettyString()
    } catch (e: IOException) {
      return e.message.toString()
    }
  }

  override fun stopInstance(): String {
    try {

      // List out instances, looking for the one created by this sample app.
      val foundOurInstance: Boolean = printInstances(compute)
      if (!foundOurInstance) {
        throw Exception("instance is not found.")
      }
      // execute stop instance
      val stopInstance = compute.instances().stop(
        PROJECT_ID,
        ZONE_NAME,
        APPLICATION_NAME
      )
      val operation = stopInstance.execute()

      val error =
        blockUntilComplete(
          compute, operation,
          OPERATION_TIMEOUT_MILLIS
        )
      return if (error == null) "Success!" else error.toPrettyString()
    } catch (e: IOException) {
      return e.message.toString()
    }
  }

  override fun getInstanceList(): List<String> {
    val instances: Compute.Instances.List = compute.instances().list(PROJECT_ID, ZONE_NAME)
    val list: InstanceList = instances.execute()
    if (list.items == null) {
      return emptyList()
    }
    // TODO: enum化
    return list.items.filter { instance: Instance -> instance.status == "RUNNING" }
      .map { instance: Instance -> instance.name }
  }

  /**
   * Print available machine instances.
   *
   * @param compute The main API access point
   * @return `true` if the instance created by this sample app is in the list
   */
  @Throws(IOException::class)
  private fun printInstances(compute: Compute): Boolean {
    val instances: Compute.Instances.List = compute.instances().list(
      PROJECT_ID,
      ZONE_NAME
    )
    val list: InstanceList = instances.execute()
    var found = false
    if (list.items == null) {
      println(
        "No instances found. Sign in to the Google Developers Console and create "
                + "an instance at: https://console.developers.google.com/"
      )
    } else {
      for (instance in list.items) {
        if (instance.name == APPLICATION_NAME) {
          found = true
        }
      }
    }
    return found
  }

  /**
   * Wait until `operation` is completed.
   * @param compute the `Compute` object
   * @param operation the operation returned by the original request
   * @param timeout the timeout, in millis
   * @return the error, if any, else `null` if there was no error
   * @throws InterruptedException if we timed out waiting for the operation to complete
   * @throws IOException if we had trouble connecting
   */
  @Throws(Exception::class)
  fun blockUntilComplete(
    compute: Compute, operation: Operation?, timeout: Long
  ): Operation.Error? {
    var operation = operation
    val start = System.currentTimeMillis()
    val pollInterval = 5 * 1000.toLong()
    var zone = operation!!.zone // null for global/regional operations
    if (zone != null) {
      val bits = zone.split("/".toRegex()).toTypedArray()
      zone = bits[bits.size - 1]
    }
    var status = operation.status
    val opId = operation.name
    while (operation != null && status != "DONE") {
      Thread.sleep(pollInterval)
      val elapsed = System.currentTimeMillis() - start
      if (elapsed >= timeout) {
        throw InterruptedException("Timed out waiting for operation to complete")
      }
      operation = if (zone != null) {
        val get =
          compute.zoneOperations()[PROJECT_ID, zone, opId]
        get.execute()
      } else {
        val get =
          compute.globalOperations()[PROJECT_ID, opId]
        get.execute()
      }
      if (operation != null) {
        status = operation.status
      }
    }
    return operation?.error
  }

  companion object {
    private const val APPLICATION_NAME = ""
    private const val PROJECT_ID = ""
    private const val ZONE_NAME = ""
    private const val OPERATION_TIMEOUT_MILLIS: Long = 60 * 1000
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
  }

}
