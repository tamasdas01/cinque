import Loader from '../ui/Loader'

type DetectingScreenProps = {
  imageSrc: string
}

export default function DetectingScreen({ imageSrc }: DetectingScreenProps) {
  return (
    <section className="screen detecting">
      <div className="preview">
        <img src={imageSrc} alt="Selected crop" />
      </div>
      <Loader label="Detecting disease..." />
    </section>
  )
}
