'use client'

import { BookOpen, Users } from 'lucide-react'
import { useApp } from '@/lib/app-context'
import { Button } from '@/components/ui/button'

export function WelcomeScreen() {
  const { setOnboarded } = useApp()

  return (
    <div className="min-h-screen flex flex-col items-center justify-center px-8 py-12 bg-background">
      {/* Logo / Icon */}
      <div className="relative mb-8">
        <div className="w-24 h-24 rounded-3xl bg-primary/10 flex items-center justify-center">
          <BookOpen className="w-12 h-12 text-primary" strokeWidth={1.5} />
        </div>
        <div className="absolute -bottom-2 -right-2 w-8 h-8 rounded-full bg-accent flex items-center justify-center">
          <Users className="w-4 h-4 text-accent-foreground" strokeWidth={2} />
        </div>
      </div>

      {/* App Name */}
      <h1 className="text-3xl font-semibold text-foreground mb-3 text-center text-balance">
        Quran Keluarga
      </h1>

      {/* Tagline */}
      <p className="text-muted-foreground text-center text-base leading-relaxed mb-12 max-w-[280px] text-pretty">
        Pantau bacaan keluarga, bersama-sama.
      </p>

      {/* Features */}
      <div className="w-full max-w-xs space-y-4 mb-12">
        <FeatureItem
          icon="📖"
          title="Baca Al-Quran"
          description="Tampilan bersih, tanpa iklan"
        />
        <FeatureItem
          icon="👨‍👩‍👧‍👦"
          title="Progress Keluarga"
          description="Pantau bacaan bersama"
        />
        <FeatureItem
          icon="🔖"
          title="Bookmark Otomatis"
          description="Lanjut dari terakhir baca"
        />
      </div>

      {/* Actions */}
      <div className="w-full max-w-xs space-y-3">
        <Button
          onClick={() => setOnboarded(true)}
          className="w-full h-14 text-base font-medium rounded-2xl bg-primary hover:bg-primary/90 text-primary-foreground"
        >
          Mulai
        </Button>
        <Button
          variant="outline"
          onClick={() => setOnboarded(true)}
          className="w-full h-14 text-base font-medium rounded-2xl border-2 border-border hover:bg-secondary"
        >
          Gabung Keluarga
        </Button>
      </div>

      {/* Footer */}
      <p className="mt-auto pt-8 text-xs text-muted-foreground/70">
        Aplikasi ini bebas iklan.
      </p>
    </div>
  )
}

function FeatureItem({
  icon,
  title,
  description,
}: {
  icon: string
  title: string
  description: string
}) {
  return (
    <div className="flex items-center gap-4 p-4 rounded-2xl bg-card border border-border/50">
      <div className="text-2xl w-10 h-10 flex items-center justify-center rounded-xl bg-secondary">
        {icon}
      </div>
      <div>
        <p className="font-medium text-foreground text-sm">{title}</p>
        <p className="text-xs text-muted-foreground">{description}</p>
      </div>
    </div>
  )
}
