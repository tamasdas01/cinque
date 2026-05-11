import { CTA_TRY_ANOTHER } from '../data/appCopy'
import { formatLabel } from '../utils/labels'
import ActionButton from '../ui/ActionButton'
import Card from '../ui/Card'

type ResultScreenProps = {
  imageSrc: string
  label: string
  confidence: number
  onReset: () => void
}

export default function ResultScreen({
  imageSrc,
  label,
  confidence,
  onReset,
}: ResultScreenProps) {
  const confidencePct = Math.round(confidence * 1000) / 10
  const isHealthy = label.toLowerCase().includes('healthy')
  const formatted = formatLabel(label)

  // Split "Plant - Disease" for display
  const parts = formatted.split(' - ')
  const plantName = parts[0]
  const diseaseName = parts.length > 1 ? parts.slice(1).join(' - ') : null

  return (
    <section className="screen result">
      <div className="preview">
        <img src={imageSrc} alt="Crop sample" />
      </div>
      <Card className="result-card">
        <p className="result-eyebrow">{plantName}</p>
        <p className={`result-label ${isHealthy ? 'label-healthy' : 'label-diseased'}`}>
          {isHealthy ? 'Healthy' : (diseaseName ?? formatted)}
        </p>
        <div className="confidence-row">
          <span className="confidence-bar-wrap">
            <span
              className={`confidence-bar-fill ${isHealthy ? 'fill-healthy' : 'fill-diseased'}`}
              style={{ width: `${confidencePct}%` }}
            />
          </span>
          <span className="confidence-value">{confidencePct}%</span>
        </div>
        <p className="result-confidence">Confidence</p>
        {!isHealthy && (
          <p className="result-advice">
            Consult an agricultural expert for treatment of {diseaseName ?? formatted}.
          </p>
        )}
        <ActionButton label={CTA_TRY_ANOTHER} onClick={onReset} />
      </Card>
    </section>
  )
}
