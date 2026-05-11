export type PredictionResult = {
  label: string
  confidence: number
  index: number
}

export type ModelLoadInfo = {
  inputWidth: number
  inputHeight: number
  labelsCount: number
}
