export const asDataUrl = (base64: string) => `data:image/jpeg;base64,${base64}`

export const stripDataUrl = (data: string) =>
  data.includes(',') ? data.split(',').pop() ?? data : data
