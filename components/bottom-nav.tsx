'use client'

import { Home, BookOpen, Users, Settings } from 'lucide-react'
import { useApp, Screen } from '@/lib/app-context'
import { cn } from '@/lib/utils'

const navItems: { id: Screen; label: string; icon: typeof Home }[] = [
  { id: 'home', label: 'Beranda', icon: Home },
  { id: 'reader', label: 'Baca', icon: BookOpen },
  { id: 'family', label: 'Keluarga', icon: Users },
  { id: 'settings', label: 'Pengaturan', icon: Settings },
]

export function BottomNav() {
  const { state, setScreen } = useApp()

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-50 bg-card/95 backdrop-blur-md border-t border-border safe-area-bottom">
      <div className="flex items-center justify-around max-w-md mx-auto h-16 px-2">
        {navItems.map(item => {
          const Icon = item.icon
          const isActive = state.screen === item.id
          return (
            <button
              key={item.id}
              onClick={() => setScreen(item.id)}
              className={cn(
                'flex flex-col items-center justify-center gap-1 flex-1 h-full transition-colors',
                isActive ? 'text-primary' : 'text-muted-foreground'
              )}
            >
              <Icon className={cn('w-5 h-5', isActive && 'stroke-[2.5px]')} />
              <span className={cn('text-[10px] font-medium', isActive && 'font-semibold')}>
                {item.label}
              </span>
            </button>
          )
        })}
      </div>
    </nav>
  )
}
