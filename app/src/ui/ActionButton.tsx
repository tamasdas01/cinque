type ActionButtonProps = {
  label: string
  onClick?: () => void
  variant?: 'primary' | 'secondary'
  disabled?: boolean
}

export default function ActionButton({
  label,
  onClick,
  variant = 'primary',
  disabled = false,
}: ActionButtonProps) {
  return (
    <button
      className={`action-button ${variant}`}
      type="button"
      onClick={onClick}
      disabled={disabled}
    >
      <span>{label}</span>
    </button>
  )
}
