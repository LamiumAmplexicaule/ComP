package net.henbit.comp.dialog

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler
import java.util.stream.Collectors
import java.util.zip.GZIPInputStream
import javax.swing.JComponent


class RunNewCommandDialog(project: Project) : DialogWrapper(project) {

    var contestId: String = ""

    @Suppress("unused")
    class Contest {
        val id: String = ""

        @JsonProperty("start_epoch_second")
        val startEpochSecond: Long = 0

        @JsonProperty("duration_second")
        val durationSecond: Long = 0

        val title: String = ""

        @JsonProperty("rate_change")
        val rateChange: String = ""
    }

    init {
        init()
        title = "Select Contest"
    }

    override fun createCenterPanel(): JComponent {
        val request: HttpRequest = HttpRequest
            .newBuilder(URI.create("https://kenkoooo.com/atcoder/resources/contests.json"))
            .header("Accept-Encoding", "gzip")
            .build()
        val bodyHandler: BodyHandler<InputStream> = HttpResponse.BodyHandlers.ofInputStream()
        val response: HttpResponse<InputStream> = HttpClient.newBuilder().build().send(request, bodyHandler)
        val bufferedReader = BufferedReader(InputStreamReader(GZIPInputStream(response.body())))
        val jsonData = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()))

        val contests = jacksonObjectMapper().readValue(jsonData, Array<Contest>::class.java)
        val contestNames = contests.map { contest -> contest.title.trim() }.sorted()
        val contestProperty = MutableProperty(
            { contestId },
            { v -> contestId = contests.find { it.title == v }?.id ?: throw Exception() }
        )

        return panel {
            row("Contest:") {
                comboBox(contestNames)
                    .gap(RightGap.SMALL)
                    .resizableColumn()
                    .horizontalAlign(HorizontalAlign.FILL)
                    .bindItem(contestProperty)
            }
        }
    }

}

private fun Cell<ComboBox<String>>.bindItem(property: MutableProperty<String>): Cell<ComboBox<String>> {
    return bind(
        { component -> component.selectedItem as String },
        { component, value -> component.setSelectedItem(value) },
        property
    )
}
