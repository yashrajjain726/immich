package app.alextran.immich.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.core.net.toUri
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.appwidget.*
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import app.alextran.immich.R
import app.alextran.immich.widget.model.*
import java.io.File

class PhotoWidget : GlanceAppWidget() {
  override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val prefs = currentState<MutablePreferences>()

      val imageUUID = prefs[kImageUUID]
      val subtitle = prefs[kSubtitleText]
      val deeplinkURL = prefs[kDeeplinkURL]?.toUri()
      val widgetState = prefs[kWidgetState]
      var bitmap: Bitmap? = null

      if (imageUUID != null) {
        // fetch a random photo from server
        val file = File(context.cacheDir, imageFilename(imageUUID))

        if (file.exists()) {
          bitmap = loadScaledBitmap(file, 500, 500)
        }
      }

      // WIDGET CONTENT
      Box(
        modifier = GlanceModifier
          .fillMaxSize()
          .background(GlanceTheme.colors.background)
          .clickable {
            val intent = Intent(Intent.ACTION_VIEW, deeplinkURL ?: "immich://".toUri())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
          }
      ) {
        if (bitmap != null) {
          Image(
            provider = ImageProvider(bitmap),
            contentDescription = "Widget Image",
            contentScale = ContentScale.Crop,
            modifier = GlanceModifier.fillMaxSize()
          )

          if (!subtitle.isNullOrBlank()) {
            Column(
              verticalAlignment = Alignment.Bottom,
              horizontalAlignment = Alignment.Start,
              modifier = GlanceModifier
                .fillMaxSize()
                .padding(12.dp)
            ) {
              Text(
                text = subtitle,
                style = TextStyle(
                  color = ColorProvider(Color.White),
                  fontSize = 16.sp
                ),
                modifier = GlanceModifier
                  .background(ColorProvider(Color(0x99000000))) // 60% black
                  .padding(8.dp)
                  .cornerRadius(8.dp)
              )
            }
          }
        } else {
          Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Image(
              provider = ImageProvider(R.drawable.splash),
              contentDescription = null,
            )

            if (widgetState == WidgetState.LOG_IN.toString()) {
              Box(
                modifier = GlanceModifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("Log in to your Immich server", style = TextStyle(textAlign = TextAlign.Center, color = GlanceTheme.colors.primary))
              }
            } else {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = GlanceModifier.fillMaxWidth().padding(16.dp)
              ) {
                CircularProgressIndicator(
                  modifier = GlanceModifier.size(12.dp)
                )

                Spacer(modifier = GlanceModifier.width(8.dp))

                Text("Loading widget...", style = TextStyle(textAlign = TextAlign.Center, color = GlanceTheme.colors.primary))
              }
            }
          }
        }
      }
    }
  }
}
