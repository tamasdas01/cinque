import { APP_SUBTITLE, APP_TITLE } from '../data/appCopy'
import Loader from '../ui/Loader'

export default function SplashScreen() {
  return (
    <section className="screen splash">
      <div className="brand">
        <span className="brand-mark" aria-hidden="true" />
        <div>
          <h1>{APP_TITLE}</h1>
          <p>{APP_SUBTITLE}</p>
        </div>
      </div>
      <Loader label="Loading model..." />
    </section>
  )
}
