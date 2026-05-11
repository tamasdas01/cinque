import { useEffect, useState } from 'react'
import { Camera, CameraResultType, CameraSource } from '@capacitor/camera'
import './App.css'
import { CropDisease } from './model/cropDisease'
import type { PredictionResult } from './model/types'
import { asDataUrl } from './utils/image'
import DetectingScreen from './screens/DetectingScreen'
import ConfirmScreen from './screens/ConfirmScreen'
import HomeScreen from './screens/HomeScreen'
import ResultScreen from './screens/ResultScreen'
import SplashScreen from './screens/SplashScreen'

const INITIAL_STAGE = 'splash'

type AppStage = 'splash' | 'home' | 'confirm' | 'detecting' | 'result'

type SelectedImage = {
  base64: string
  dataUrl: string
}

function App() {
  const [stage, setStage] = useState<AppStage>(INITIAL_STAGE)
  const [selectedImage, setSelectedImage] = useState<SelectedImage | null>(null)
  const [result, setResult] = useState<PredictionResult | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false

    const loadModel = async () => {
      try {
        await CropDisease.load()
        if (!cancelled) {
          setStage('home')
        }
      } catch (err: any) {
        console.error(err);
        if (!cancelled) {
          setError(`Model could not be loaded: ${err.message || err}`)
          setStage('home')
        }
      }
    }

    loadModel()
    return () => {
      cancelled = true
    }
  }, [])

  const pickImage = async (source: CameraSource) => {
    setError(null)
    try {
      const photo = await Camera.getPhoto({
        quality: 82,
        allowEditing: false,
        resultType: CameraResultType.Base64,
        source,
      })

      if (!photo.base64String) {
        setError('No image data found. Please try again.')
        return
      }

      const base64 = photo.base64String
      setSelectedImage({ base64, dataUrl: asDataUrl(base64) })
      setResult(null)
      setStage('confirm')
    } catch (err) {
      setError('Image selection was cancelled or failed.')
    }
  }

  const handleDetect = async () => {
    if (!selectedImage) return
    setError(null)
    setStage('detecting')

    try {
      const prediction = await CropDisease.predict({
        base64: selectedImage.base64,
      })
      setResult(prediction)
      setStage('result')
    } catch (err: any) {
      console.error(err);
      setError(`Detection failed: ${err.message || err}. Please try again.`)
      setStage('confirm')
    }
  }

  const handleReset = () => {
    setSelectedImage(null)
    setResult(null)
    setStage('home')
  }

  return (
    <div className={`app-shell stage-${stage}`}>
      <main className="app">
        {stage === 'splash' && <SplashScreen />}
        {stage === 'home' && (
          <HomeScreen
            onCamera={() => pickImage(CameraSource.Camera)}
            onGallery={() => pickImage(CameraSource.Photos)}
          />
        )}
        {stage === 'confirm' && selectedImage && (
          <ConfirmScreen
            imageSrc={selectedImage.dataUrl}
            onConfirm={handleDetect}
            onCancel={handleReset}
          />
        )}
        {stage === 'detecting' && selectedImage && (
          <DetectingScreen imageSrc={selectedImage.dataUrl} />
        )}
        {stage === 'result' && selectedImage && result && (
          <ResultScreen
            imageSrc={selectedImage.dataUrl}
            label={result.label}
            confidence={result.confidence}
            onReset={handleReset}
          />
        )}
        {error && <div className="error-banner">{error}</div>}
      </main>
    </div>
  )
}

export default App
