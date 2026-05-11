import { registerPlugin } from '@capacitor/core'
import type { ModelLoadInfo, PredictionResult } from './types'

export interface CropDiseasePlugin {
  load(): Promise<ModelLoadInfo>
  predict(options: { base64: string }): Promise<PredictionResult>
}

export const CropDisease = registerPlugin<CropDiseasePlugin>('CropDisease')
