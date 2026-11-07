# TV Home Assistant panel

`tv-home-card.js` is served by HA at `/local/tv-home-card.js` and used by the `tv-dashboard/main` panel. The card provides large kettle controls, live WAN rates/history, and an on-demand entrance camera.

Camera: `camera.entrance_tv` is a separate Generic Camera using `rtsp://127.0.0.1:8554/entrance_tv`. The original `camera.entrance` remains unchanged. The go2rtc `entrance_sd` source copies the existing Xiaomi source with `subtype=sd`. The `entrance_tv` source runs:

```
exec:/opt/go2rtc/ffmpeg -hide_banner -v error -rtsp_transport tcp -i rtsp://127.0.0.1:8554/entrance_sd -an -vf fps=10,scale=640:360 -c:v libx264 -preset ultrafast -tune zerolatency -pix_fmt yuv420p -g 20 -f rtsp {output}
```

The picture is live 640x360 at 10 fps, without audio. The player requests an authenticated HLS URL from HA only when Start is pressed. Stop, card removal or page hiding releases playback. HA controls its stream worker idle timeout. No repeated JPEG conversion is used.

`tv-hls.mjs` is hls.js 1.6.13 from the video-dev/hls.js project; see HLS-LICENSE.txt.
