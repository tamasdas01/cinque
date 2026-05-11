export const formatLabel = (label: string) =>
  label
    .replace(/___/g, ' - ')
    .replace(/_/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
