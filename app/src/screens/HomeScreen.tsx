import {
  APP_SUBTITLE,
  APP_TITLE,
  CTA_CAMERA,
  CTA_GALLERY,
} from '../data/appCopy'
import ActionButton from '../ui/ActionButton'

type HomeScreenProps = {
  onCamera: () => void
  onGallery: () => void
}

export default function HomeScreen({ onCamera, onGallery }: HomeScreenProps) {
  return (
    <section className="screen home">
      <div className="headline">
        <p className="eyebrow">Offline Assistant</p>
        <h1>{APP_TITLE}</h1>
        <p className="subtitle">{APP_SUBTITLE}</p>
      </div>
      <div className="button-stack">
        <ActionButton label={CTA_CAMERA} onClick={onCamera} />
        <ActionButton label={CTA_GALLERY} onClick={onGallery} variant="secondary" />
      </div>
      <div className="hint">No internet required. Results stay on device.</div>
    </section>
  )
}
