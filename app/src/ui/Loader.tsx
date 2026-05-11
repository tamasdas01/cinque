type LoaderProps = {
  label?: string
}

export default function Loader({ label = 'Working...' }: LoaderProps) {
  return (
    <div className="loader">
      <div className="spinner" aria-hidden="true" />
      <p>{label}</p>
    </div>
  )
}
