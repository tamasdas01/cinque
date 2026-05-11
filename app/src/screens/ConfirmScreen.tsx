import { CTA_CANCEL, CTA_DETECT, CTA_YES } from '../data/appCopy'
import ActionButton from '../ui/ActionButton'
import Card from '../ui/Card'

type ConfirmScreenProps = {
  imageSrc: string
  onConfirm: () => void
  onCancel: () => void
}

export default function ConfirmScreen({
  imageSrc,
  onConfirm,
  onCancel,
}: ConfirmScreenProps) {
  return (
    <section className="screen confirm">
      <div className="preview">
        <img src={imageSrc} alt="Selected crop" />
      </div>
      <Card className="confirm-card">
        <h2>{CTA_DETECT}</h2>
        <p>We will analyze this image and predict the disease.</p>
        <div className="button-row">
          <ActionButton label={CTA_YES} onClick={onConfirm} />
          <ActionButton label={CTA_CANCEL} onClick={onCancel} variant="secondary" />
        </div>
      </Card>
    </section>
  )
}
